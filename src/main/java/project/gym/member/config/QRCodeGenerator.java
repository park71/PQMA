package project.gym.member.config;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeGenerator {

        @Value("${qr.code.save.path}")
        private String qrCodeSavePath;

        public void generateQRCode(String qrCodeText, String fileName) throws WriterException, IOException {
            int width = 300; // QR 코드 너비
            int height = 300; // QR 코드 높이
            String fileType = "png"; // QR 코드 파일 타입

            // QR 코드 작성기를 사용하여 QR 코드 생성
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // UTF-8 인코딩 설정
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, width, height, hints);

            // QR 코드 파일 저장 경로
            Path path = FileSystems.getDefault().getPath(qrCodeSavePath + File.separator + fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, fileType, path);

            System.out.println("QR 코드 생성 완료: " + path.toString());
        }
    }


