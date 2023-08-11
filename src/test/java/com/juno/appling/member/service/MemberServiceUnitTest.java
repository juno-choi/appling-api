package com.juno.appling.member.service;

import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.member.domain.dto.*;
import com.juno.appling.member.domain.entity.Member;
import com.juno.appling.member.domain.entity.Seller;
import com.juno.appling.member.repository.MemberApplySellerRepository;
import com.juno.appling.member.repository.MemberRepository;
import com.juno.appling.member.repository.SellerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith({MockitoExtension.class})
class MemberServiceUnitTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberApplySellerRepository memberApplySellerRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private SellerRepository sellerRepository;

    HttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("회원이 존재하지 않을경우 회원 수정 실패")
    void patchMemberFail1(){
        // given
        PatchMemberDto patchMemberDto = new PatchMemberDto( null, null, null, null, null);

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        // when
        Throwable throwable = catchThrowable(() -> memberService.patchMember(patchMemberDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 수령인 정보 등록에 실패")
    void postRecipientFail1(){
        // given
        PostRecipientDto recipient = new PostRecipientDto();
        // when
        Throwable throwable = catchThrowable(() -> memberService.postRecipient(recipient, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 수령인 정보 불러오기에 실패")
    void getRecipientFail1(){
        // given
        // when
        Throwable throwable = catchThrowable(() -> memberService.getRecipient(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 등록에 실패")
    void postSellerFail1(){
        // given
        PostSellerDto sellerDto = new PostSellerDto();
        // when
        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("이미 판매자 정보를 등록한 경우 등록에 실패")
    void postSellerFail2(){
        // given
        PostSellerDto sellerDto = new PostSellerDto("회사명", "010-1234-4321", "회사 주소", "email@mail.com");
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        given(sellerRepository.findByMember(any())).willReturn(Optional.of(Seller.of(member, "회사명", "010-1234-1233", "회사 주소", "email@mail.com")));
        // when
        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("판매자 신청을 완료");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 수정에 실패")
    void putSellerFail1(){
        // given
        PutSellerDto sellerDto = new PutSellerDto();
        // when
        Throwable throwable = catchThrowable(() -> memberService.putSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 수정에 실패")
    void putSellerFail2(){
        // given
        PutSellerDto sellerDto = new PutSellerDto();
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        // when
        Throwable throwable = catchThrowable(() -> memberService.putSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 조회에 실패")
    void getSellerFail1(){
        // given
        // when
        Throwable throwable = catchThrowable(() -> memberService.getSeller(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 조회에 실패")
    void getSellerFail2(){
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        // when
        Throwable throwable = catchThrowable(() -> memberService.getSeller(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 소개 등록에 실패")
    void postIntroduceFail1(){
        // given
        PostIntroduceDto postIntroduceDto = new PostIntroduceDto("https://s3.com/html/test1.html");
        // when
        Throwable throwable = catchThrowable(() -> memberService.postIntroduce(postIntroduceDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("판매자 정보가 존재하지 않을경우 소개 등록에 실패")
    void postIntroduceFail2(){
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        PostIntroduceDto postIntroduceDto = new PostIntroduceDto("https://s3.com/html/test1.html");
        // when
        Throwable throwable = catchThrowable(() -> memberService.postIntroduce(postIntroduceDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 판매자");
    }

}