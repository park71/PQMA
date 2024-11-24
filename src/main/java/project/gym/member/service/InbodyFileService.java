package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.gym.member.entity.InbodyEntity;
import project.gym.member.entity.MemberEntity;
import project.gym.member.repository.InbodyRepository;
import project.gym.member.repository.MemberRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Service
public class InbodyFileService {

    @Autowired
    private InbodyRepository inbodyFileRepository;
    @Autowired
    private MemberRepository memberRepository;

    public void saveFile(String pdfUrl, String fileName) {
        try {
            URL url = new URL(pdfUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-API-Key", "rVVcGg4bTVaf9Eg8BeYJZONW4ZrI3bZtEM65WFcElNc=");


            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, Paths.get("resources/inbody/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // InbodyFileEntity를 데이터베이스에 저장
//    public InbodyEntity saveInbodyFile(InbodyEntity inbodyFile) {
//        // 파일 정보가 유효한지 검사
//        if (inbodyFile == null || inbodyFile.getPhone() == null || inbodyFile.getFile_path() == null) {
//            throw new IllegalArgumentException("Invalid Inbody file data");
//        }
//
//        // 데이터베이스에 저장
//        return inbodyFileRepository.save(inbodyFile);
//    }



    public void saveInbodyData(String name, String birth, MultipartFile image) {
        // MemberEntity에서 이름과 생년월일로 회원 조회
        MemberEntity member = memberRepository.findByNameAndBirth(name, LocalDate.parse(birth));
        if (member != null) {
            try {
                // 이미지 파일을 서버에 저장
                String uploadDirectory = "src/main/resources/static/inbody/"; // 이미지가 저장될 디렉터리 경로
                String filename = image.getOriginalFilename(); // 업로드된 파일의 이름
                File targetFile = new File(uploadDirectory, filename);

                // 파일 저장
                image.transferTo(targetFile);

                // InbodyEntity에 데이터 저장
                InbodyEntity inbodyEntity = new InbodyEntity();
                inbodyEntity.setName(name);
                inbodyEntity.setBirths(LocalDate.parse(birth));
                inbodyEntity.setFile_path(targetFile.getAbsolutePath()); // 파일 경로 저장
                inbodyFileRepository.save(inbodyEntity);

                // MemberEntity에 inbody 필드에 데이터 저장
                member.setInbody(targetFile.getAbsolutePath());
                memberRepository.save(member);

            } catch (IOException e) {
                // 파일 저장 실패 처리
                e.printStackTrace();
            }
        }
    }

    public List<InbodyEntity> findInbodyFilesByMemberName(String name) {
        return inbodyFileRepository.findByName(name);
    }
}
