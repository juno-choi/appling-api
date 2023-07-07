package com.juno.appling.service.member;

import com.juno.appling.CommonTest;
import com.juno.appling.domain.dto.member.*;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.member.Recipient;
import com.juno.appling.domain.enums.member.RecipientInfoStatus;
import com.juno.appling.domain.vo.MessageVo;
import com.juno.appling.domain.vo.member.BuyerVo;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.domain.vo.member.RecipientListVo;
import com.juno.appling.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.juno.appling.CommonTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
        request.addHeader(AUTHORIZATION, "Bearer "+login.getAccessToken());

        PatchMemberDto patchMemberDto = new PatchMemberDto(changeBirth, changeName, changePassword, "수정되버림", null);
        // when
        MessageVo messageVo = memberService.patchMember(patchMemberDto, request);
        // then
        assertThat(messageVo.getMessage()).contains("회원 정보 수정 성공");
    }



    @Test
    @DisplayName("회원 구매자 정보 등록에 성공")
    void postBuyerSuccess1(){
        // given
        String email = "buyer@mail.com";
        String password = "password";

        JoinDto joinDto = new JoinDto(email, password, "구매자", "구매자야", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer "+login.getAccessToken());

        PostBuyerDto postBuyerDto = new PostBuyerDto("구매할사람", "buyer@mail.com", "01012341234");
        // when
        MessageVo messageVo = memberService.postBuyer(postBuyerDto, request);
        // then
        assertThat(messageVo.getMessage()).contains("구매자 정보 등록 성공");
    }

    @Test
    @DisplayName("회원 구매자 정보 불러오기에 성공")
    void getBuyerSuccess1(){
        // given
        String email = "buyer2@mail.com";
        String password = "password";

        JoinDto joinDto = new JoinDto(email, password, "구매자정보", "구매자정보등록", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        Member member = memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer "+login.getAccessToken());

        PostBuyerDto postBuyerDto = new PostBuyerDto("구매할사람", "buyer@mail.com", "01012341234");
        memberService.postBuyer(postBuyerDto, request);
        // when
        BuyerVo buyer = memberService.getBuyer(request);
        // then
        assertThat(buyer.getEmail()).isEqualTo(postBuyerDto.getEmail());
        assertThat(buyer.getTel()).isEqualTo(postBuyerDto.getTel());
        assertThat(buyer.getName()).isEqualTo(postBuyerDto.getName());
    }

    @Test
    @DisplayName("회원 구매자 정보 수정에 성공")
    void putBuyerSuccess1(){
        // given
        String email = "buyer3@mail.com";
        String password = "password";

        JoinDto joinDto = new JoinDto(email, password, "구매자정보", "구매자정보등록", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        Member member = memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer "+login.getAccessToken());

        PostBuyerDto postBuyerDto = new PostBuyerDto("구매할사람", "buyer@mail.com", "01012341234");
        memberService.postBuyer(postBuyerDto, request);
        BuyerVo originBuyer = memberService.getBuyer(request);

        PutBuyerDto putBuyerDto = new PutBuyerDto(originBuyer.getId(), "수정된사람", "buyer@mail.com", "01043214123");
        // when
        MessageVo messageVo = memberService.putBuyer(putBuyerDto);
        // then
        assertThat(messageVo.getMessage()).contains("수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    @Transactional
    void getRecipientList(){
        // given
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL.getVal(), PASSWORD.getVal());
        LoginVo login = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL.getVal()).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "주소", "01012341234", RecipientInfoStatus.NORMAL);
        Recipient recipient2 = Recipient.of(member, "수령인2", "주소2", "01012341234", RecipientInfoStatus.NORMAL);

        member.getRecipientList().add(recipient1);
        member.getRecipientList().add(recipient2);

        request.addHeader(AUTHORIZATION, "Bearer "+login.getAccessToken());
        // when
        RecipientListVo recipient = memberService.getRecipient(request);
        // then
        assertThat(recipient.getList()
                .get(0)
                .getName()
        ).isEqualTo(recipient2.getName());

    }
}
