package com.juno.appling.service.member;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.member.MemberApplySeller;
import com.juno.appling.domain.enums.member.MemberApplySellerStatus;
import com.juno.appling.domain.enums.member.Role;
import com.juno.appling.domain.vo.MessageVo;
import com.juno.appling.domain.vo.member.MemberVo;
import com.juno.appling.repository.member.MemberApplySellerRepository;
import com.juno.appling.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;

    private static final String TYPE = "Bearer ";


    public MemberVo member(HttpServletRequest request) {
        String token = resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);

        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        return MemberVo.builder()
                .memberId(findMember.getId())
                .email(findMember.getEmail())
                .role(findMember.getRole())
                .createdAt(findMember.getCreatedAt())
                .modifiedAt(findMember.getModifiedAt())
                .name(findMember.getName())
                .nickname(findMember.getNickname())
                .snsType(findMember.getSnsType())
                .build();
    }
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TYPE)) {
            return bearerToken.substring(TYPE.length());
        }
        return null;
    }

    @Transactional
    public MessageVo applySeller(HttpServletRequest request) {
        String token = resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);

        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(MemberApplySeller.of(memberId));

        // TODO 임시로 판매자 권인 승인하도록 로직을 만들어둠 나중에는 admin에서 승인해주어야 함
        permitSeller(memberId, saveMemberApply);

        return MessageVo.builder()
                .message("임시적으로 SELLER 즉시 승인")
                .build();
    }

    private void permitSeller(Long memberId, MemberApplySeller saveMemberApply) {
        saveMemberApply.patchApplyStatus(MemberApplySellerStatus.PERMIT);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.")
        );
        member.patchMemberRole(Role.SELLER);
    }
}
