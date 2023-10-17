package com.juno.appling.member.application;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.dto.request.JoinRequest;
import com.juno.appling.member.dto.request.LoginRequest;
import com.juno.appling.member.dto.request.PatchMemberRequest;
import com.juno.appling.member.dto.request.PutSellerRequest;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Recipient;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.enums.RecipientInfoStatus;
import com.juno.appling.member.dto.response.LoginResponse;
import com.juno.appling.member.dto.response.RecipientListResponse;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.SellerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import static com.juno.appling.Base.MEMBER_EMAIL;
import static com.juno.appling.Base.MEMBER_LOGIN;
import static com.juno.appling.Base.SELLER_EMAIL;
import static com.juno.appling.Base.SELLER_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(scripts = {"/sql/clear.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
})
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SellerRepository sellerRepository;

    MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("회원 정보 수정에 성공")
    void patchMemberSuccess1() {
        // given
        String email = "patch@mail.com";
        String password = "password";
        String changePassword = "password2";
        String changeName = "수정함";
        String changeBirth = "19941030";

        JoinRequest joinRequest = new JoinRequest(email, password, "수정자", "수정할거야", "19991010");
        joinRequest.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinRequest));
        LoginRequest loginRequest = new LoginRequest(email, password);
        LoginResponse login = memberAuthService.login(loginRequest);
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        PatchMemberRequest patchMemberRequest = new PatchMemberRequest(changeBirth, changeName, changePassword,
            "수정되버림", null);
        // when
        MessageVo messageVo = memberService.patchMember(patchMemberRequest, request);
        // then
        assertThat(messageVo.message()).contains("회원 정보 수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    void getRecipientList() throws Exception {
        // given
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "1234", "주소", "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        Thread.sleep(10L);
        Recipient recipient2 = Recipient.of(member, "수령인2", "1234", "주소2", "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);

        member.getRecipientList().add(recipient1);
        member.getRecipientList().add(recipient2);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());
        // when
        RecipientListResponse recipient = memberService.getRecipient(request);
        // then
        assertThat(recipient.getList()
            .get(0)
            .getName()
        ).isEqualTo(recipient2.getName());

    }

    @Test
    @DisplayName("판매자 정보 수정 성공")
    void putSeller() {
        // given
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());

        String changeCompany = "변경 회사명";
        PutSellerRequest putSellerRequest = new PutSellerRequest(changeCompany, "01012341234", "4321", "변경된 주소", "상세 주소",
            "mail@mail.com");
        // when
        MessageVo messageVo = memberService.putSeller(putSellerRequest, request);
        // then
        assertThat(messageVo.message()).contains("수정 성공");
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Seller seller = sellerRepository.findByMember(member).get();
        assertThat(seller.getCompany()).isEqualTo(changeCompany);

    }
}
