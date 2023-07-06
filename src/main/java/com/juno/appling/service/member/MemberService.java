package com.juno.appling.service.member;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.member.PatchMemberDto;
import com.juno.appling.domain.dto.member.PostBuyerInfoDto;
import com.juno.appling.domain.dto.member.PutBuyerInfoDto;
import com.juno.appling.domain.entity.member.BuyerInfo;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.member.MemberApplySeller;
import com.juno.appling.domain.enums.member.MemberApplySellerStatus;
import com.juno.appling.domain.enums.member.Role;
import com.juno.appling.domain.vo.MessageVo;
import com.juno.appling.domain.vo.member.BuyerInfoVo;
import com.juno.appling.domain.vo.member.MemberVo;
import com.juno.appling.repository.member.BuyerInfoRepository;
import com.juno.appling.repository.member.MemberApplySellerRepository;
import com.juno.appling.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BuyerInfoRepository buyerInfoRepository;

    public MemberVo member(HttpServletRequest request) {
        Member findMember = getMember(request);

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

    @Transactional
    public MessageVo applySeller(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);

        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(MemberApplySeller.of(memberId));

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

    @Transactional
    public MessageVo patchMember(PatchMemberDto patchMemberDto, HttpServletRequest request){
        Member member = getMember(request);

        String birth = Optional.ofNullable(patchMemberDto.getBirth()).orElse("").replaceAll("-", "").trim();
        String name = Optional.ofNullable(patchMemberDto.getName()).orElse("").trim();
        String nickname = Optional.ofNullable(patchMemberDto.getNickname()).orElse("").trim();
        String password = Optional.ofNullable(patchMemberDto.getPassword()).orElse("").trim();
        if(!password.isEmpty()){
            password = passwordEncoder.encode(password);
        }
        member.patchMember(birth, name, nickname, password);

        return MessageVo.builder()
                .message("회원 정보 수정 성공")
                .build();
    }

    @Transactional
    public MessageVo postBuyerInfo(PostBuyerInfoDto postBuyerInfoDto, HttpServletRequest request){
        Member member = getMember(request);

        if(member.getBuyerInfo() != null){
            throw new IllegalArgumentException("이미 구매자 정보를 등록하셨습니다.");
        }

        BuyerInfo buyerInfo = buyerInfoRepository.save(BuyerInfo.of(null, postBuyerInfoDto.getName(), postBuyerInfoDto.getEmail(), postBuyerInfoDto.getTel()));
        member.putBuyerInfo(buyerInfo);

        return MessageVo.builder()
                .message("구매자 정보 등록 성공")
                .build();
    }

    private Member getMember(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.")
        );
    }

    public BuyerInfoVo getBuyerInfo(HttpServletRequest request){
        Member member = getMember(request);
        BuyerInfo buyerInfo = member.getBuyerInfo();
        return BuyerInfoVo.builder()
                .id(buyerInfo.getId())
                .email(buyerInfo.getEmail())
                .name(buyerInfo.getName())
                .tel(buyerInfo.getTel())
                .createdAt(buyerInfo.getCreatedAt())
                .modifiedAt(buyerInfo.getModifiedAt())
                .build();
    }

    @Transactional
    public BuyerInfoVo putBuyerInfo(PutBuyerInfoDto putBuyerInfoDto, HttpServletRequest request){
        Long buyerInfoId = putBuyerInfoDto.getId();
        BuyerInfo buyerInfo = buyerInfoRepository.findById(buyerInfoId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 구매자 정보입니다.")
        );
        buyerInfo.put(putBuyerInfoDto.getName(), putBuyerInfoDto.getEmail(), putBuyerInfoDto.getTel());
        return BuyerInfoVo.builder()
                .id(buyerInfo.getId())
                .email(buyerInfo.getEmail())
                .name(buyerInfo.getName())
                .tel(buyerInfo.getTel())
                .createdAt(buyerInfo.getCreatedAt())
                .modifiedAt(buyerInfo.getModifiedAt())
                .build();
    }
}
