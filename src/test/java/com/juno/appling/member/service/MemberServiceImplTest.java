package com.juno.appling.member.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.controller.request.JoinRequest;
import com.juno.appling.member.controller.request.LoginRequest;
import com.juno.appling.member.controller.request.PatchMemberRequest;
import com.juno.appling.member.controller.request.PutSellerRequest;
import com.juno.appling.member.controller.response.LoginResponse;
import com.juno.appling.member.controller.response.RecipientListResponse;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.entity.RecipientEntity;
import com.juno.appling.member.enums.RecipientInfoStatus;
import com.juno.appling.member.port.MemberJpaRepository;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.product.port.SellerJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import static com.juno.appling.Base.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql", "/sql/introduce.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
public class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SellerJpaRepository sellerJpaRepository;

    MockHttpServletRequest request = new MockHttpServletRequest();

    @AfterEach
    void cleanup() {
        memberJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 정보 수정에 성공")
    void patchMemberSuccess1() {
        // given
        String email = "patch@mail.com";
        String password = "password";
        String changePassword = "password2";
        String changeName = "수정함";
        String changeBirth = "19941030";

        JoinRequest joinRequest = JoinRequest.builder()
            .email(email)
            .password(password)
            .name("수정자")
            .nickname("수정할거야")
            .birth("19991010")
            .build();
        joinRequest.passwordEncoder(passwordEncoder);
        memberJpaRepository.save(MemberEntity.of(joinRequest));
        LoginRequest loginRequest = LoginRequest.builder().email(email).password(password).build();
        LoginResponse login = memberAuthService.login(loginRequest);
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        PatchMemberRequest patchMemberRequest = PatchMemberRequest.builder()
            .birth(changeBirth)
            .name(changeName)
            .password(changePassword)
            .nickname("수정되어버림")
            .build();
        // when
        MessageVo messageVo = memberService.patchMember(patchMemberRequest, request);
        // then
        assertThat(messageVo.getMessage()).contains("회원 정보 수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    void getRecipientList() throws Exception {
        // given
        MemberEntity memberEntity = memberJpaRepository.findByEmail(MEMBER_EMAIL).get();

        RecipientEntity recipientEntity1 = RecipientEntity.of(memberEntity, "수령인1", "1234", "주소", "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        Thread.sleep(10L);
        RecipientEntity recipientEntity2 = RecipientEntity.of(memberEntity, "수령인2", "1234", "주소2", "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);

        memberEntity.getRecipientList().add(recipientEntity1);
        memberEntity.getRecipientList().add(recipientEntity2);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());
        // when
        RecipientListResponse recipient = memberService.getRecipient(request);
        // then
        assertThat(recipient.getList().size()).isEqualTo(2);
        assertThat(recipient.getList()
            .get(0)
            .getName()
        ).isEqualTo(recipientEntity2.getName());

    }

    @Test
    @DisplayName("판매자 정보 수정 성공")
    void putSeller() {
        // given
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());

        String changeCompany = "변경 회사명";
        PutSellerRequest putSellerRequest = PutSellerRequest.builder()
            .company(changeCompany)
            .tel("01012341234")
            .zonecode("4321")
            .address("변경 주소")
            .addressDetail("상세 주소")
            .email("mail@mail.com")
            .build();

        // when
        MessageVo messageVo = memberService.putSeller(putSellerRequest, request);
        // then
        assertThat(messageVo.getMessage()).contains("수정 성공");
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        assertThat(sellerEntity.getCompany()).isEqualTo(changeCompany);

    }
}
