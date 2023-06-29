package com.juno.appling.controller.member;

import com.juno.appling.BaseTest;
import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.dto.member.PatchMemberDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.enums.ResultCode;
import com.juno.appling.domain.enums.member.Role;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.repository.member.MemberRepository;
import com.juno.appling.service.member.MemberAuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerDocs extends BaseTest {
    
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;

    private final static String PREFIX = "/api/member";
    private final static String EMAIL = "juno@member.com";
    private final static String PASSWORD = "password";

    @Test
    @DisplayName(PREFIX)
    void member() throws Exception {
        //given
        LoginDto loginDto = new LoginDto(EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);
        //when
        ResultActions resultActions = mock.perform(
                get(PREFIX)
                .header(AUTHORIZATION, "Bearer "+loginVo.getAccessToken())
        ).andDo(print());

        //then
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

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
                        fieldWithPath("data.role").type(JsonFieldType.STRING).description(String.format("권한 (일반 유저 : %s, 판매자 : %s)", Role.MEMBER, Role.SELLER)),
                        fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                        fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일")
                )
        ));
    }

    @Test
    @DisplayName(PREFIX+"/seller")
    void applySeller() throws Exception {
        //given
        JoinDto joinDto = new JoinDto(EMAIL, PASSWORD, "name", "nick", "19941030");
        memberAuthService.join(joinDto);
        LoginDto loginDto = new LoginDto(EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);
        //when
        ResultActions resultActions = mock.perform(
                post(PREFIX+"/seller")
                        .header(AUTHORIZATION, "Bearer "+loginVo.getAccessToken())
        ).andDo(print());

        //then
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

        resultActions.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.message").type(JsonFieldType.STRING).description("성공")
                )
        ));
    }

    @Test
    @DisplayName(PREFIX)
    void patchMember() throws Exception {
        //given
        String email = "patch@mail.com";
        String password = "password";
        String changePassword = "password2";
        String changeName = "수정함";
        String changeBirth = "19941030";

        JoinDto joinDto = new JoinDto(email, password, "수정자", "수정할거야", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberAuthService.login(loginDto);

        PatchMemberDto patchMemberDto = new PatchMemberDto(changeBirth, changeName, changePassword, "수정되버림", null);

        //when
        ResultActions resultActions = mock.perform(
                patch(PREFIX)
                        .header(AUTHORIZATION, "Bearer "+loginVo.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(patchMemberDto))
        ).andDo(print());


        //then
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

        resultActions.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token")
                ),
                requestFields(
                        fieldWithPath("birth").type(JsonFieldType.STRING).description("생일").optional(),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional(),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임").optional(),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("회원 상태(아직 사용하지 않음) ex)탈퇴").optional()
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.message").type(JsonFieldType.STRING).description("결과 메세지")
                )
        ));
    }

}