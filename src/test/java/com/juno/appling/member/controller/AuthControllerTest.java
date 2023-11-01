package com.juno.appling.member.controller;

import static com.juno.appling.Base.objectMapper;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.juno.appling.RestdocsBaseTest;
import com.juno.appling.member.controller.request.JoinRequest;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.infrastruceture.MemberRepository;
import java.nio.charset.StandardCharsets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.CONCURRENT)
class AuthControllerTest extends RestdocsBaseTest {

    @Autowired
    private MemberRepository memberRepository;

    private final static String PREFIX = "/api/auth";

    @Nested
    class join {

        @Test
        @DisplayName("중복 회원 회원가입 실패")
        void joinFail() throws Exception {
            //given
            JoinRequest joinRequest = JoinRequest.builder()
                .email("join@mail.com")
                .password("password")
                .name("name")
                .nickname("nick")
                .birth("19941030")
                .build()
            memberRepository.save(Member.of(joinRequest));
            //when
            ResultActions resultActions = mock.perform(
                post(PREFIX + "/join").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(joinRequest))
            ).andDo(print());
            //then
            String contentAsString = resultActions.andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
            Assertions.assertThat(contentAsString).contains("400");
        }
    }
}