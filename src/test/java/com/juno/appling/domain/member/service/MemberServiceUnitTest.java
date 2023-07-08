package com.juno.appling.domain.member.service;

import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.domain.member.dto.PatchMemberDto;
import com.juno.appling.domain.member.dto.PostRecipientDto;
import com.juno.appling.domain.member.repository.MemberApplySellerRepository;
import com.juno.appling.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.catchThrowable;
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