package com.juno.appling.controller.member;

import com.juno.appling.BaseTest;
import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.repository.member.MemberRepository;
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

@SpringBootTest
class AuthControllerTest extends BaseTest {
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