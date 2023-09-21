package com.juno.appling.member.application;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.s3.S3Service;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.Introduce;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.enums.IntroduceStatus;
import com.juno.appling.member.dto.request.*;
import com.juno.appling.member.domain.IntroduceRepository;
import com.juno.appling.member.domain.MemberApplySellerRepository;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.SellerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    private S3Service s3Service;
    @Mock
    private Environment env;
    @Mock
    private IntroduceRepository introduceRepository;

    HttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("회원이 존재하지 않을경우 회원 수정 실패")
    void patchMemberFail1() {
        // given
        PatchMemberRequest patchMemberRequest = new PatchMemberRequest(null, null, null, null, null);

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        // when
        Throwable throwable = catchThrowable(
                () -> memberService.patchMember(patchMemberRequest, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 수령인 정보 등록에 실패")
    void postRecipientFail1() {
        // given
        PostRecipientRequest recipient = new PostRecipientRequest();
        // when
        Throwable throwable = catchThrowable(() -> memberService.postRecipient(recipient, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 수령인 정보 불러오기에 실패")
    void getRecipientFail1() {
        // given
        // when
        Throwable throwable = catchThrowable(() -> memberService.getRecipient(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 등록에 실패")
    void postSellerFail1() {
        // given
        PostSellerRequest sellerDto = new PostSellerRequest();
        // when
        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("이미 판매자 정보를 등록한 경우 등록에 실패")
    void postSellerFail2() {
        // given
        PostSellerRequest sellerDto = new PostSellerRequest("회사명", "010-1234-4321", "1234", "회사 주소", "상세 주소",
                "email@mail.com");
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinRequest);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        given(sellerRepository.findByMember(any())).willReturn(Optional.of(
                Seller.of(member, "회사명", "010-1234-1233", "1234", "회사 주소", "상세 주소","email@mail.com")));
        // when
        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("판매자 신청을 완료");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 수정에 실패")
    void putSellerFail1() {
        // given
        PutSellerRequest sellerDto = new PutSellerRequest();
        // when
        Throwable throwable = catchThrowable(() -> memberService.putSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 수정에 실패")
    void putSellerFail2() {
        // given
        PutSellerRequest sellerDto = new PutSellerRequest();
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinRequest);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        // when
        Throwable throwable = catchThrowable(() -> memberService.putSeller(sellerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 조회에 실패")
    void getSellerFail1() {
        // given
        // when
        Throwable throwable = catchThrowable(() -> memberService.getSeller(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 판매자 정보 조회에 실패")
    void getSellerFail2() {
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinRequest);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        // when
        Throwable throwable = catchThrowable(() -> memberService.getSeller(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 소개 등록에 실패")
    void postIntroduceFail1() {
        // given
        PostIntroduceRequest postIntroduceRequest = new PostIntroduceRequest("제목",
                "https://s3.com/html/test1.html");
        // when
        Throwable throwable = catchThrowable(
                () -> memberService.postIntroduce(postIntroduceRequest, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("판매자 정보가 존재하지 않을경우 소개 등록에 실패")
    void postIntroduceFail2() {
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinRequest);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        PostIntroduceRequest postIntroduceRequest = new PostIntroduceRequest("제목",
                "https://s3.com/html/test1.html");
        // when
        Throwable throwable = catchThrowable(
                () -> memberService.postIntroduce(postIntroduceRequest, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("판매자 정보가 존재하는 경우 update로 수정")
    void postIntroduceSuccess() {
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinRequest);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        PostIntroduceRequest postIntroduceRequest = new PostIntroduceRequest("제목",
                "https://s3.com/html/test1.html");
        Seller seller = Seller.of(member, "compnay", "01012341234", "123", "address", "상세 주소","mail@mail.com");
        given(sellerRepository.findByMember(member)).willReturn(Optional.of(seller));
        Introduce introduce = Introduce.of(seller, "subject", "url", IntroduceStatus.USE);
        given(introduceRepository.findBySeller(any())).willReturn(Optional.of(introduce));
        // when
        MessageVo messageVo = memberService.postIntroduce(postIntroduceRequest, request);
        // then
        assertThat(messageVo.message()).contains("성공");
    }

    @Test
    @DisplayName("소개 페이지를 등록하지 않았을땐 소개글 불러오기 실패")
    void getIntroduceFail1() {
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinRequest);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(
                Optional.of(Seller.of(member, "", "", "", "", "", "")));
        // when
        Throwable throwable = catchThrowable(() -> memberService.getIntroduce(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("소개 페이지를 먼저 등록");
    }

    @Test
    @DisplayName("소개 페이지 불러오기 성공")
    void getIntroduceFail2() {
        // given
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick", "19941030");
        String html = "<html></html>";
        Member member = Member.of(joinRequest);
        Seller seller = Seller.of(member, "", "", "", "", "", "");

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(Optional.of(seller));
        given(introduceRepository.findBySeller(any())).willReturn(Optional.of(
                Introduce.of(seller, "subject",
                        "https://appling-s3-bucket.s3.ap-northeast-2.amazonaws.com/html/1/20230815/172623_0.html",
                        IntroduceStatus.USE)));
        given(env.getProperty(eq("cloud.s3.bucket"))).willReturn("s3-bucket");
        given(s3Service.getObject(anyString(), anyString())).willReturn(html);
        // when
        String introduce = memberService.getIntroduce(request);
        // then
        assertThat(introduce).isEqualTo(html);
    }

    @Test
    @DisplayName("소개 페이지 정보가 존재하지 않을 경우 실패")
    void getIntroduce2Fail1() {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> memberService.getIntroduce(1L))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("유효하지 않은 판매자입니다");
    }
}