package com.juno.appling.common.service;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.juno.appling.common.controller.response.UploadResponse;
import com.juno.appling.global.s3.S3Service;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.enums.MemberRole;
import com.juno.appling.member.port.MemberJpaRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@ExtendWith(MockitoExtension.class)
class CommonS3ServiceImplUnitTest {

    @InjectMocks
    private CommonS3ServiceImpl commonS3Service;
    @Mock
    private Environment env;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberJpaRepository memberJpaRepository;
    @Mock
    private S3Service s3Service;

    private MockHttpServletRequest request = new MockHttpServletRequest();
    private List<MultipartFile> files = new LinkedList<>();
    private String fileName1 = "test1.txt";
    private String fileName2 = "test2.txt";
    private String fileName3 = "test3.txt";
    private String s3Url = "https://s3.aws.url";
    @BeforeEach
    void setUp() {
        files.clear();
        files.add(new MockMultipartFile("test1", fileName1, StandardCharsets.UTF_8.name(),
            "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", fileName2, StandardCharsets.UTF_8.name(),
            "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", fileName3, StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));
    }
    @Test
    @DisplayName("upload image 실패")
    void uploadImageFail1() {
        //given
        //when
        Throwable throwable = catchThrowable(
            () -> commonS3Service.s3UploadFile(files, "image/%s/%s/", request));

        //then
        Assertions.assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("upload image 성공")
    void uploadImageSuccess1() {
        //given

        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity = new MemberEntity(1L, "email@mail.com", "password", "nickname", "name", "19941030",
            MemberRole.SELLER, null, null, now, now);
        given(memberJpaRepository.findById(any())).willReturn(Optional.of(memberEntity));
        given(env.getProperty(anyString())).willReturn(s3Url);
        List<String> list = new LinkedList<>();
        list.add(fileName1);
        list.add(fileName2);
        list.add(fileName3);
        given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        //when
        UploadResponse uploadResponse = commonS3Service.s3UploadFile(files, "image/%s/%s/", request);
        //then
        Assertions.assertThat(uploadResponse.getUrl()).contains(s3Url);
    }

    @Test
    @DisplayName("upload html 실패")
    void uploadHtmlFail1() {
        //given
        //when
        Throwable throwable = catchThrowable(
            () -> commonS3Service.s3UploadFile(files, "html/%s/%s/", request));

        //then
        Assertions.assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("upload html 성공")
    void uploadHtmlSuccess1() {
        //given
        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity = new MemberEntity(1L, "email@mail.com", "password", "nickname", "name", "19941030",
            MemberRole.SELLER, null, null, now, now);
        given(memberJpaRepository.findById(any())).willReturn(Optional.of(memberEntity));
        given(env.getProperty(anyString())).willReturn(s3Url);
        List<String> list = new LinkedList<>();
        list.add(fileName1);
        list.add(fileName2);
        list.add(fileName3);
        given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        //when
        UploadResponse uploadResponse = commonS3Service.s3UploadFile(files, "html/%s/%s/", request);
        //then
        Assertions.assertThat(uploadResponse.getUrl()).contains(s3Url);
    }
}