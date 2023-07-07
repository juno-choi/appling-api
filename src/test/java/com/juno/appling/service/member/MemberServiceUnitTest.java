package com.juno.appling.service.member;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.member.PatchMemberDto;
import com.juno.appling.domain.dto.member.PostBuyerDto;
import com.juno.appling.domain.dto.member.PostRecipientDto;
import com.juno.appling.domain.dto.member.PutBuyerDto;
import com.juno.appling.domain.entity.member.Buyer;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.vo.member.BuyerVo;
import com.juno.appling.repository.member.BuyerRepository;
import com.juno.appling.repository.member.MemberApplySellerRepository;
import com.juno.appling.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.catchThrowable;
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
    private BuyerRepository buyerRepository;

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
                .hasMessageContaining("존재하지 않는 회원");
    }


    @Test
    @DisplayName("회원이 존재하지 않을경우 구매자 정보 입력에 실패")
    void postBuyerFail1(){
        // given
        PostBuyerDto postBuyerDto = new PostBuyerDto("최준호", "buyer_info@appling.com", "01012341234");

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        // when
        Throwable throwable = catchThrowable(() -> memberService.postBuyer(postBuyerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("이미 구매자 정보를 입력했을 경우 입력에 실패")
    void postBuyerFail2(){
        // given
        PostBuyerDto postBuyerDto = new PostBuyerDto("최준호", "buyer_info@appling.com", "01012341234");

        given(tokenProvider.getMemberId(request)).willReturn(1L);
        Member member = new Member();
        member.putBuyer(Buyer.of(null, "구매자", "buyer@mail.com", "01012341234"));
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        // when
        Throwable throwable = catchThrowable(() -> memberService.postBuyer(postBuyerDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 구매자 정보를 등록");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 구매자 정보 불러오기에 실패")
    void getBuyerFail1(){
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        // when
        Throwable throwable = catchThrowable(() -> memberService.getBuyer(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("구매자 정보 존재하지 않을 경우 빈값으로 불러오기에 성공")
    void getBuyerSuccess1(){
        // given
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(new Member()));
        // when
        BuyerVo buyer = memberService.getBuyer(request);
        // then
        assertThat(buyer.getName()).isEqualTo("");
        assertThat(buyer.getEmail()).isEqualTo("");
        assertThat(buyer.getTel()).isEqualTo("");
    }

    @Test
    @DisplayName("구매자 정보가 존재하지 않을 경우 수정에 실패")
    void putBuyerFail1(){
        // given
        PutBuyerDto putBuyerDto = new PutBuyerDto();
        // when
        Throwable throwable = catchThrowable(() -> memberService.putBuyer(putBuyerDto));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 구매자 정보");
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
                .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을경우 수령인 정보 불러오기에 실패")
    void getRecipientFail1(){
        // given
        // when
        Throwable throwable = catchThrowable(() -> memberService.getRecipient(request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원");
    }
}