package com.juno.appling.domain.common.service;

import com.juno.appling.domain.member.dto.LoginDto;
import com.juno.appling.domain.common.record.UploadRecord;
import com.juno.appling.domain.member.record.LoginRecord;
import com.juno.appling.domain.member.service.MemberAuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CommonS3ServiceTest {
    @Autowired
    private CommonS3Service commonS3Service;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private Environment env;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @Disabled("s3에 실제로 데이터가 올라가는 테스트임")
    @DisplayName("이미 등록 성공")
    void uploadImage() {
        //given
        LoginDto loginDto = new LoginDto("seller@appling.com", "password");
        LoginRecord login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer "+login.accessToken());
        List<MultipartFile> files = new LinkedList<>();
        String fileName1 = "test1.txt";
        String fileName2 = "test2.txt";
        String fileName3 = "test3.txt";

        files.add(new MockMultipartFile("test1", fileName1, StandardCharsets.UTF_8.name(), "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", fileName2, StandardCharsets.UTF_8.name(), "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", fileName3, StandardCharsets.UTF_8.name(), "3".getBytes(StandardCharsets.UTF_8)));

        //when
        UploadRecord uploadRecord = commonS3Service.uploadImage(files, request);

        //then
        Assertions.assertThat(uploadRecord.imageUrl()).contains(env.getProperty("cloud.s3.url"));
    }
}