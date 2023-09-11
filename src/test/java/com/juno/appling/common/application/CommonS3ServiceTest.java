package com.juno.appling.common.application;

import com.juno.appling.common.dto.response.UploadResponse;
import com.juno.appling.member.dto.request.LoginRequest;
import com.juno.appling.member.dto.response.LoginResponse;
import com.juno.appling.member.application.MemberAuthService;
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
        LoginRequest loginRequest = new LoginRequest("seller@appling.com", "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());
        List<MultipartFile> files = new LinkedList<>();
        String fileName1 = "test1.txt";
        String fileName2 = "test2.txt";
        String fileName3 = "test3.txt";

        files.add(new MockMultipartFile("test1", fileName1, StandardCharsets.UTF_8.name(),
            "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", fileName2, StandardCharsets.UTF_8.name(),
            "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", fileName3, StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));

        //when
        UploadResponse uploadResponse = commonS3Service.s3UploadFile(files, "image/%s/%s/", request);

        //then
        Assertions.assertThat(uploadResponse.getUrl()).contains(env.getProperty("cloud.s3.url"));
    }
}