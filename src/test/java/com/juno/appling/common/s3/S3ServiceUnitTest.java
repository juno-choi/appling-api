package com.juno.appling.common.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class S3ServiceUnitTest {
    @InjectMocks
    private S3Service s3Service;

    @Mock
    private S3Client s3Client;

    @Test
    @DisplayName("이미지 등록에 성공")
    void putObjectSuccess() {
        //given
        String eTag = "e-tag";

        List<MultipartFile> files = new LinkedList<>();
        files.add(new MockMultipartFile("test1", "test1.txt", StandardCharsets.UTF_8.name(), "1".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", "test2.txt", StandardCharsets.UTF_8.name(), "2".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", "test3.txt", StandardCharsets.UTF_8.name(), "3".getBytes(StandardCharsets.UTF_8)));

        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).willReturn(
                (PutObjectResponse) PutObjectResponse.builder()
                        .eTag(eTag)
                        .serverSideEncryption("AES-256")
                        .sdkHttpResponse(SdkHttpResponse.builder().statusText("OK").build())
                        .build()
        );

        //when
        List<String> putObjectList = s3Service.putObject("juno/test/", files);
        //then
        assertThat(putObjectList).isNotEmpty();
    }

    @Test
    @DisplayName("이미지 등록에 실패")
    void putObjectFail() {
        //given
        String eTag = "e-tag";

        List<MultipartFile> files = new LinkedList<>();
        files.add(new MockMultipartFile("test1", "test1.txt", StandardCharsets.UTF_8.name(), "1".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", "test2.txt", StandardCharsets.UTF_8.name(), "2".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", "test3.txt", StandardCharsets.UTF_8.name(), "3".getBytes(StandardCharsets.UTF_8)));

        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).willReturn(
                (PutObjectResponse) PutObjectResponse.builder()
                        .eTag(eTag)
                        .serverSideEncryption("AES-256")
                        .sdkHttpResponse(SdkHttpResponse.builder().statusText("").build())
                        .build()
        );

        //when
        Throwable throwable = catchThrowable(() -> s3Service.putObject("juno/test/", files));

        //then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasMessage("AWS에 파일을 올리는데 실패했습니다.");

    }
}