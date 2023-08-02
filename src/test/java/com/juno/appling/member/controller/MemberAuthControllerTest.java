package com.juno.appling.member.controller;

import com.juno.appling.ControllerBaseTest;
import com.juno.appling.member.domain.dto.JoinDto;
import com.juno.appling.member.domain.entity.Member;
import com.juno.appling.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberAuthControllerTest extends ControllerBaseTest {
    @Autowired
    private MemberRepository memberRepository;

    private final static String PREFIX = "/api/auth";

    @Nested
    class join{
        @Test
        @DisplayName("중복 회원 회원가입 실패")
        void joinFail() throws Exception{
            //given
            JoinDto joinDto = new JoinDto("join@mail.com", "password", "name", "nick", "19941030");
            memberRepository.save(Member.of(joinDto));
            //when
            ResultActions resultActions = mock.perform(
                    post(PREFIX+"/join").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(joinDto))
            ).andDo(print());
            //then
            String contentAsString = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            Assertions.assertThat(contentAsString).contains("400");
        }
    }
}