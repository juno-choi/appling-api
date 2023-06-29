package com.juno.appling.service.member;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.member.PatchMemberDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.repository.member.MemberApplySellerRepository;
import com.juno.appling.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
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

    HttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("회원이 존재하지 않을경우 회원 수정 실패")
    void patchMemberFail1(){
        // given
        PatchMemberDto patchMemberDto = new PatchMemberDto(0L, null, null, null, null, null);

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        // when
        Throwable throwable = catchThrowable(() -> memberService.patchMember(patchMemberDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원이 정보가 다를 경우 회원 수정 실패")
    void patchMemberFail2(){
        // given
        PatchMemberDto patchMemberDto = new PatchMemberDto(0L, null, null, null, null, null);

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(new Member(1L, "mail@mail.com", "", "", "", "", null, null, null, null, null)));
        // when
        Throwable throwable = catchThrowable(() -> memberService.patchMember(patchMemberDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("다른 회원의 정보를 수정할 수 없습니다");
    }
}