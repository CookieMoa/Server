package com.example.springserver.global.s3;

import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${spring.cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    // Base64로 받은 데이터 디코딩해서 S3에 업로드 후 이미지 URL 반환
    public String uploadFileImage(MultipartFile imageFile) {
        try {
            String extension = getFileExtension(imageFile.getContentType());
            if (extension == null) {
                throw new GeneralException(ErrorStatus.INVALID_IMAGE_FORMAT);
            }

            String fileName = "profile-images/" + UUID.randomUUID() + extension;

            // AWS S3 업로드 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(imageFile.getContentType())
                    .build();

            // 파일 업로드 (InputStream 사용)
            try (var inputStream = imageFile.getInputStream()) {
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, imageFile.getSize()));
            }

            // CloudFront URL 반환
            return cloudFrontDomain + "/" + fileName;
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.IMAGE_UPLOAD_FAILED);
        }
    }

    // 파일 확장자 결정
    private String getFileExtension(String contentType) {
        if (contentType == null) return null;
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> null;
        };
    }

    // basicImg URL 반환
    public String getBasicImgUrl() {
        return cloudFrontDomain + "/profile-images/basicImg.jpeg";
    }
}