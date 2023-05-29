package com.juno.appling.controller.member;

import com.juno.appling.BaseTest;
import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.repository.member.MemberRepository;
import com.juno.appling.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
class MemberControllerDocs extends BaseTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("/member/join")
    void join() throws Exception {
        //given
        JoinDto joinDto = new JoinDto("juno@member.com", "password", "name", "nick", "19941030");

        //when
        ResultActions resultActions = mock.perform(
                post("/member/join").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDto))
        ).andDo(print());

        //then
        resultActions.andDo(docs.document(
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("birth").type(JsonFieldType.STRING).description("생년월일 ex) 19941030")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
                )
        ));
    }

    @Test
    @DisplayName("/member/login")
    void login() throws Exception{
        //given
        String email = "juno@member.com";
        String password = "password";
        LoginDto loginDto = new LoginDto(email, password);

        //when
        ResultActions resultActions = mock.perform(
                post("/member/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
        ).andDo(print());

        //then
        resultActions.andDo(docs.document(
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.type").type(JsonFieldType.STRING).description("token type"),
                        fieldWithPath("data.access_token").type(JsonFieldType.STRING).description("access token"),
                        fieldWithPath("data.refresh_token").type(JsonFieldType.STRING).description("refresh token (기한 : 발급일로 부터 7일)"),
                        fieldWithPath("data.access_token_expired").type(JsonFieldType.NUMBER).description("access token expired")
                )
        ));
    }

    @Test
    @DisplayName("/member/refresh/{refresh_token}")
    void refresh() throws Exception{
        //given
        String email = "juno3@member.com";
        String password = "password";
        JoinDto joinDto = new JoinDto(email, password, "name", "nick", "19941030");
        memberService.join(joinDto);
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberService.login(loginDto);

        //when
        ResultActions resultActions = mock.perform(
                RestDocumentationRequestBuilders.get("/member/refresh/{refresh_token}", loginVo.getRefreshToken())
        ).andDo(print());

        //then
        resultActions.andDo(docs.document(
                pathParameters(
                        parameterWithName("refresh_token").description("refresh token")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.type").type(JsonFieldType.STRING).description("token type"),
                        fieldWithPath("data.access_token").type(JsonFieldType.STRING).description("access token"),
                        fieldWithPath("data.refresh_token").type(JsonFieldType.STRING).description("refresh token (기한 : 발급일로 부터 7일)"),
                        fieldWithPath("data.access_token_expired").type(JsonFieldType.NUMBER).description("access token expired")
                )
        ));
    }

    @Test
    @DisplayName("/member")
    void member() throws Exception {
        //given
        String email = "juno4@member.com";
        String password = "password";
        JoinDto joinDto = new JoinDto(email, password, "name", "nick", "19941030");
        memberService.join(joinDto);
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberService.login(loginDto);

        //when
        ResultActions resultActions = mock.perform(
                RestDocumentationRequestBuilders.get("/member")
                .header(AUTHORIZATION, "Bearer "+loginVo.getAccessToken())
        ).andDo(print());

        //then
        resultActions.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.member_id").type(JsonFieldType.NUMBER).description("member id"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("email"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("nickname"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("role"),
                        fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                        fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일")
                )
        ));

    }
}