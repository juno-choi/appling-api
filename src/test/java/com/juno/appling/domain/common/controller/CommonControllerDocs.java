package com.juno.appling.domain.common.controller;

import com.juno.appling.ControllerBaseTest;
import com.juno.appling.config.s3.S3Service;
import com.juno.appling.domain.member.dto.LoginDto;
import com.juno.appling.domain.member.service.MemberAuthService;
import com.juno.appling.domain.member.vo.LoginVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class CommonControllerDocs extends ControllerBaseTest {
    @Autowired
    private MemberAuthService memberAuthService;

    @MockBean
    private S3Service s3Service;

    private final String PREFIX = "/api/common";

    @Test
    @DisplayName(PREFIX + "/image")
    void uploadImage() throws Exception{
        //given
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);
        List<String> list = new LinkedList<>();
        list.add("image/1/20230606/202101.png");
        given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        //when
        ResultActions perform = mock.perform(
                multipart(PREFIX + "/image")
                        .file(new MockMultipartFile("image", "test1.png", MediaType.APPLICATION_FORM_URLENCODED_VALUE, "123".getBytes(StandardCharsets.UTF_8)))
                        .header(AUTHORIZATION, "Bearer "+login.accessToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        //then
        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token")
                ),
                requestParts(
                        partWithName("image").description("업로드 이미지")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.url").type(JsonFieldType.STRING).description("업로드 된 이미지 url")
                )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/html")
    void uploadHtml() throws Exception{
        //given
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);
        List<String> list = new LinkedList<>();
        list.add("html/1/20230606/202101.html");
        given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        //when
        ResultActions perform = mock.perform(
                multipart(PREFIX + "/html")
                        .file(new MockMultipartFile("html", "test1.html", MediaType.APPLICATION_FORM_URLENCODED_VALUE, "123".getBytes(StandardCharsets.UTF_8)))
                        .header(AUTHORIZATION, "Bearer "+login.accessToken())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        //then
        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token")
                ),
                requestParts(
                        partWithName("html").description("업로드 html 파일")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.url").type(JsonFieldType.STRING).description("업로드 된 html url")
                )
        ));
    }
}