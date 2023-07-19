package com.juno.appling.domain.member.service;

import com.juno.appling.domain.member.dto.*;
import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.member.entity.Recipient;
import com.juno.appling.domain.member.entity.Seller;
import com.juno.appling.domain.member.enums.RecipientInfoStatus;
import com.juno.appling.config.base.MessageVo;
import com.juno.appling.domain.member.repository.SellerRepository;
import com.juno.appling.domain.member.vo.LoginVo;
import com.juno.appling.domain.member.vo.RecipientListVo;
import com.juno.appling.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.juno.appling.CommonTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
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
    void patchMemberSuccess1(){
        // given
        String email = "patch@mail.com";
        String password = "password";
        String changePassword = "password2";
        String changeName = "수정함";
        String changeBirth = "19941030";

        JoinDto joinDto = new JoinDto(email, password, "수정자", "수정할거야", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer "+login.accessToken());

        PatchMemberDto patchMemberDto = new PatchMemberDto(changeBirth, changeName, changePassword, "수정되버림", null);
        // when
        MessageVo messageVo = memberService.patchMember(patchMemberDto, request);
        // then
        assertThat(messageVo.message()).contains("회원 정보 수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    void getRecipientList(){
        // given
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL.getVal(), PASSWORD.getVal());
        LoginVo login = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL.getVal()).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "주소", "01012341234", RecipientInfoStatus.NORMAL);
        Recipient recipient2 = Recipient.of(member, "수령인2", "주소2", "01012341234", RecipientInfoStatus.NORMAL);

        member.getRecipientList().add(recipient1);
        member.getRecipientList().add(recipient2);

        request.addHeader(AUTHORIZATION, "Bearer "+login.accessToken());
        // when
        RecipientListVo recipient = memberService.getRecipient(request);
        // then
        assertThat(recipient.list()
                .get(0)
                .name()
        ).isEqualTo(recipient2.getName());

    }

    @Test
    @DisplayName("판매자 정보 수정 성공")
    void putSeller(){
        // given
        LoginDto loginDto = new LoginDto(SELLER_EMAIL.getVal(), PASSWORD.getVal());
        LoginVo login = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(SELLER_EMAIL.getVal()).get();

        request.addHeader(AUTHORIZATION, "Bearer "+login.accessToken());

        String changeCompany = "변경 회사명";
        PutSellerDto putSellerDto = new PutSellerDto(changeCompany, "01012341234", "변경된 주소", "mail@mail.com");
        // when
        MessageVo messageVo = memberService.putSeller(putSellerDto, request);
        // then
        assertThat(messageVo.message()).contains("수정 성공");
        Seller seller = sellerRepository.findByMember(member).get();
        assertThat(seller.getCompany()).isEqualTo(changeCompany);

    }
}
