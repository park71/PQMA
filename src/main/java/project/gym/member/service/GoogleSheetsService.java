package project.gym.member.service;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.*;

@Service
public class GoogleSheetsService {

    private final MariaDBService mariaDBService;

    private static final String APPLICATION_NAME = "My Spring Boot App";
    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/spreadsheets");
    private static final String SPREADSHEET_ID = "19o6Gmm4vxIYjjo1whKV_1fX446-gQ1RGsSqkG2EZ8Po";

    private static final String RANGE = "!A2:AK"; // 데이터 범위 A2부터 전부
    public GoogleSheetsService(MariaDBService mariaDBService) {
        this.mariaDBService = mariaDBService;
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        ServiceAccountCredentials credentials = (ServiceAccountCredentials) ServiceAccountCredentials.fromStream(
                new ClassPathResource("priqma-project-441511-7c12c2897b78.json").getInputStream()
        ).createScoped(SCOPES);

        // HttpCredentialsAdapter로 변환
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                requestInitializer
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public List<List<Object>> readSheetData(String range) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        System.out.println("Requested range: " + range);

        // API 호출 시 URL 인코딩을 직접 하지 않도록 합니다.
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        return response.getValues();
    }

    public void writeDataToSheet(List<List<Object>> data) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();

        // 데이터 입력 범위 (예: 'Sheet1' 시트의 A1 셀부터)
        String range = "!A2";

        ValueRange body = new ValueRange().setValues(data);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, range, body)
                .setValueInputOption("RAW")
                .execute();

        System.out.println("Data to be updated: " + data);

        System.out.println(result.getUpdatedCells() + " cells updated.");

    }
    //////////////////////////////////////////////

    public void syncDataToSheets() {
        try {
            // 1. MariaDB에서 remainDays가 -7 이하인 사람들 데이터 가져오기
            List<List<Object>> data = mariaDBService.fetchRemainingDaysData();

            // 2. 가져온 데이터를 Google Sheets에 기록하기
            writeDataToSheet(data);
            System.out.println("Data to be updated: " + data);

        } catch (SQLException | IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Data synchronization failed", e);
        }
    }




//    public List<Object> findMemberByPhone(String phone) throws Exception {
//        FileInputStream serviceAccountStream = new FileInputStream("path/to/service-account.json");
//        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
//                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY));
//
//        Sheets service = new Sheets.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
//                new com.google.api.client.json.JsonFactory() {
//                    @Override
//                    public JsonParser createJsonParser(InputStream inputStream) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public JsonParser createJsonParser(InputStream inputStream, Charset charset) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public JsonParser createJsonParser(String s) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public JsonParser createJsonParser(Reader reader) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public JsonGenerator createJsonGenerator(OutputStream outputStream, Charset charset) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public JsonGenerator createJsonGenerator(Writer writer) throws IOException {
//                        return null;
//                    }
//                },
//                new HttpCredentialsAdapter(credentials))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//
//        ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
//        List<List<Object>> values = response.getValues();
//
//        for (List<Object> row : values) {
//            if (row.size() > 4 && row.get(4).toString().equals(phone)) { // 전화번호는 5번째 열 (0부터 시작)
//                return row;
//            }
//        }
//        return null; // 회원 정보가 없는 경우
//    }

    public List<Map<String, Object>> findMembersByName(String name) throws Exception {

        // 쿼리가 어떻게 실행되는지 로그 찍어보기
        System.out.println("Searching for name: " + name);
        InputStream serviceAccountStream = getClass().getClassLoader().getResourceAsStream("priqma-project-441511-7c12c2897b78.json");

        if (serviceAccountStream == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }

        // 인증 설정
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY));

        // JsonFactory와 HttpTransport 설정
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance(); // JacksonFactory 사용
        HttpTransport httpTransport = new NetHttpTransport(); // NetHttpTransport 사용

        // Sheets 서비스 객체 생성
        Sheets service = new Sheets.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 구글 시트에서 데이터를 읽어옴
        ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
        List<List<Object>> values = response.getValues();
        List<Map<String, Object>> results = new ArrayList<>();


        for (List<Object> row : values) {
            System.out.println("Row Data: " + row);  // 각 행의 데이터를 출력
            if (row.size() > 2 && row.get(2).toString().trim().equalsIgnoreCase(name.trim())) {
                Map<String, Object> member = new HashMap<>();
                member.put("id", row.get(0));
                member.put("coach", row.get(1));
                member.put("name", row.get(2));
                member.put("gender", row.get(3));
                member.put("phone", row.get(4));
                member.put("birth", row.get(5));
                member.put("address", row.get(6));
                member.put("kakao", row.get(7));
                member.put("purpose", row.get(8));
                member.put("comein", row.get(9));
                member.put("credit", row.get(10));
                member.put("price", row.get(11));
                member.put("membership", row.get(12));
                member.put("memstart", row.get(13));
                member.put("memend", row.get(14));
                member.put("remainDays", row.get(15));
                member.put("locker", row.get(16));
                member.put("locknum", row.get(17));
                member.put("lockstart",row.get(18));
                member.put("lockend", row.get(19));
                member.put("shirt", row.get(20));
                member.put("shirstart", row.get(21));
                member.put("shirtend", row.get(22));
                member.put("user_id", row.get(23));
                member.put("content", row.get(24));
                member.put("status", row.get(25));
                member.put("applicationDate", row.get(26));
                member.put("signature", row.get(27));
                member.put("stat", row.get(28));
                member.put("qrCodePath", row.get(29));
                member.put("longTime", row.get(30));
                member.put("inbody", row.get(31));
                member.put("ring", row.get(32));
                member.put("profile", row.get(33));
                member.put("profileImage", row.get(34));
                member.put("restcount", row.get(35));



                results.add(member);
            }
        }

        System.out.println("검색된 회원 목록: " + results);


        return results;
    }

    public List<Map<String, Object>> findMembersByNames(String name) throws Exception {

        // 쿼리가 어떻게 실행되는지 로그 찍어보기
        System.out.println("Searching for name: " + name);
        InputStream serviceAccountStream = getClass().getClassLoader().getResourceAsStream("priqma-project-441511-7c12c2897b78.json");

        if (serviceAccountStream == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }

        // 인증 설정
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY));

        // JsonFactory와 HttpTransport 설정
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance(); // JacksonFactory 사용
        HttpTransport httpTransport = new NetHttpTransport(); // NetHttpTransport 사용

        // Sheets 서비스 객체 생성
        Sheets service = new Sheets.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 구글 시트에서 데이터를 읽어옴
        ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
        List<List<Object>> values = response.getValues();
        List<Map<String, Object>> results = new ArrayList<>();


        for (List<Object> row : values) {
            System.out.println("Row Data: " + row);  // 각 행의 데이터를 출력
            if (row.size() > 2 && row.get(2).toString().trim().equalsIgnoreCase(name.trim())) {
                Map<String, Object> member = new HashMap<>();
                member.put("name", row.get(2));
                member.put("phone", row.get(4));
                results.add(member);
            }
        }

        System.out.println("검색된 회원 목록: " + results);


        return results;
    }


}
