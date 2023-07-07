package com.juno.appling.service.member;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.member.PatchMemberDto;
import com.juno.appling.domain.dto.member.PostBuyerDto;
import com.juno.appling.domain.dto.member.PostRecipientDto;
import com.juno.appling.domain.dto.member.PutBuyerDto;
import com.juno.appling.domain.entity.member.Buyer;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.member.MemberApplySeller;
import com.juno.appling.domain.entity.member.Recipient;
import com.juno.appling.domain.enums.member.MemberApplySellerStatus;
import com.juno.appling.domain.enums.member.RecipientInfoStatus;
import com.juno.appling.domain.enums.member.Role;
import com.juno.appling.domain.vo.MessageVo;
import com.juno.appling.domain.vo.member.BuyerVo;
import com.juno.appling.domain.vo.member.MemberVo;
import com.juno.appling.domain.vo.member.RecipientListVo;
import com.juno.appling.domain.vo.member.RecipientVo;
import com.juno.appling.repository.member.BuyerRepository;
import com.juno.appling.repository.member.MemberApplySellerRepository;
import com.juno.appling.repository.member.MemberRepository;
import com.juno.appling.repository.member.RecipientRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BuyerRepository buyerRepository;
    private final RecipientRepository recipientRepository;

    private Member getMember(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.")
        );
    }

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
    public MessageVo postBuyer(PostBuyerDto postBuyerDto, HttpServletRequest request){
        Member member = getMember(request);

        if(member.getBuyer() != null){
            throw new IllegalArgumentException("이미 구매자 정보를 등록하셨습니다.");
        }

        Buyer buyer = buyerRepository.save(Buyer.of(null, postBuyerDto.getName(), postBuyerDto.getEmail(), postBuyerDto.getTel()));
        member.putBuyer(buyer);

        return MessageVo.builder()
                .message("구매자 정보 등록 성공")
                .build();
    }

    public BuyerVo getBuyer(HttpServletRequest request){
        Member member = getMember(request);
        Buyer buyer = Optional.ofNullable(member.getBuyer()).orElse(
                Buyer.ofEmpty()
        );

        return BuyerVo.builder()
                .id(buyer.getId())
                .email(buyer.getEmail())
                .name(buyer.getName())
                .tel(buyer.getTel())
                .createdAt(buyer.getCreatedAt())
                .modifiedAt(buyer.getModifiedAt())
                .build();
    }

    @Transactional
    public MessageVo putBuyer(PutBuyerDto putBuyerDto){
        Long buyerId = putBuyerDto.getId();
        Buyer buyer = buyerRepository.findById(buyerId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 구매자 정보입니다.")
        );
        buyer.put(putBuyerDto.getName(), putBuyerDto.getEmail(), putBuyerDto.getTel());
        return MessageVo.builder()
                .message("구매자 정보 수정 성공")
                .build();
    }

    @Transactional
    public MessageVo postRecipient(PostRecipientDto postRecipientDtoInfo, HttpServletRequest request){
        Member member = getMember(request);
        recipientRepository.save(Recipient.of(member, postRecipientDtoInfo.getName(), postRecipientDtoInfo.getAddress(), postRecipientDtoInfo.getTel(), RecipientInfoStatus.NORMAL));
        return MessageVo.builder()
                .message("수령인 정보 등록 성공")
                .build();
    }

    public RecipientListVo getRecipient(HttpServletRequest request){
        Member member = getMember(request);

        List<Recipient> recipientList = member.getRecipientList();
        List<RecipientVo> list = new LinkedList<>();

        for(Recipient r : recipientList){
            list.add(RecipientVo.builder()
                    .id(r.getId())
                    .address(r.getAddress())
                    .status(r.getStatus())
                    .name(r.getName())
                    .tel(r.getTel())
                    .createdAt(r.getCreatedAt())
                    .modifiedAt(r.getModifiedAt())
                    .build());
        }
        Collections.reverse(list);
        return RecipientListVo.builder()
                .list(list)
                .build();
    }
}
