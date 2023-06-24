package com.juno.appling.common.s3;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class S3ServiceTest {
    @Autowired
    private S3Service s3Service;

    @Test
    @Disabled("s3에 실제로 데이터가 올라가는 테스트")
    @DisplayName("이미지 등록에 성공")
    void putObjectSuccess() {
        //given
        List<MultipartFile> files = new LinkedList<>();
        files.add(new MockMultipartFile("test1", "test1.txt", StandardCharsets.UTF_8.name(), "1".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", "test2.txt", StandardCharsets.UTF_8.name(), "2".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", "test3.txt", StandardCharsets.UTF_8.name(), "3".getBytes(StandardCharsets.UTF_8)));
        Long userId = 1L;
        DateTimeFormatter pathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        String pathDate = now.format(pathFormatter);

        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String fileName = now.format(fileNameFormatter);

        //when
        List<String> putObjectList = s3Service.putObject(String.format("product/%s/%s/", userId, pathDate), fileName, files);
        //then
        assertThat(putObjectList).isNotEmpty();
    }
}