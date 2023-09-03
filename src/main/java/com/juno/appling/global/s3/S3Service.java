package com.juno.appling.global.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3Service {

    private final S3Client s3Client;
    private final Environment env;

    @Value("${cloud.s3.bucket}")
    private String bucketName;

    @Transactional
    public List<String> putObject(String path, String fileName, List<MultipartFile> files) {
        List<String> list = new LinkedList<>();
        int count = 0;

        for (MultipartFile file : files) {
            String originFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("");
            log.info("[putObject] file name = {}", originFileName);
            String fileExtension = originFileName.substring(originFileName.indexOf('.'));
            String contentType = file.getContentType();
            String makeFileName = String.format("%s%s_%s%s", path, fileName, count, fileExtension);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(makeFileName)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();
            PutObjectResponse response;
            try {
                response = s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(file.getBytes()));
            } catch (IOException ie) {
                log.error("파일을 읽어들이는데 에러가 발생했습니다.");
                log.error(ie.getMessage());
                throw new IllegalStateException(ie.getMessage());
            }

            if (response.sdkHttpResponse().statusText().orElse("FAIL").equals("OK")) {
                list.add(makeFileName);
            } else {
                throw new IllegalStateException("AWS에 파일을 올리는데 실패했습니다.");
            }
            count++;
        }
        return list;
    }

    public String getObject(String path, String bucketName) {
        try {
            GetObjectRequest objectRequest =
                GetObjectRequest.builder()
                    .key(path)
                    .bucket(bucketName)
                    .build();
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(objectRequest);
            // String text = new String(response.asByteArray(), StandardCharsets.UTF_8);
            // log.debug("-------- 파일 내용 --------");
            // log.debug(text);
            // log.debug("-------- 파일 내용 --------");
            return new String(response.asByteArray(), StandardCharsets.UTF_8);
        } catch (S3Exception ae) {
            log.error("AWS와 통신에 문제가 발생했습니다.");
            log.error(ae.getMessage());
            throw new RuntimeException(ae.getMessage());
        }
    }
}
