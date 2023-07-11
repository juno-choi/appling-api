package com.juno.appling.domain.member.service;

import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.domain.member.dto.PatchMemberDto;
import com.juno.appling.domain.member.dto.PostRecipientDto;
import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.member.entity.MemberApplySeller;
import com.juno.appling.domain.member.entity.Recipient;
import com.juno.appling.domain.member.enums.MemberApplySellerStatus;
import com.juno.appling.domain.member.enums.RecipientInfoStatus;
import com.juno.appling.domain.member.enums.Role;
import com.juno.appling.config.base.MessageVo;
import com.juno.appling.domain.member.record.MemberRecord;
import com.juno.appling.domain.member.record.RecipientListRecord;
import com.juno.appling.domain.member.record.RecipientRecord;
import com.juno.appling.domain.member.repository.MemberApplySellerRepository;
import com.juno.appling.domain.member.repository.MemberRepository;
import com.juno.appling.domain.member.repository.RecipientRepository;
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
    private final RecipientRepository recipientRepository;

    private Member getMember(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.")
        );
    }

    public MemberRecord member(HttpServletRequest request) {
        Member findMember = getMember(request);

        return new MemberRecord(findMember.getId(), findMember.getEmail(), findMember.getNickname(), findMember.getName(), findMember.getRole(), findMember.getSnsType(), findMember.getCreatedAt(), findMember.getModifiedAt());
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
    public MessageVo postRecipient(PostRecipientDto postRecipientDtoInfo, HttpServletRequest request){
        Member member = getMember(request);
        recipientRepository.save(Recipient.of(member, postRecipientDtoInfo.getName(), postRecipientDtoInfo.getAddress(), postRecipientDtoInfo.getTel(), RecipientInfoStatus.NORMAL));
        return MessageVo.builder()
                .message("수령인 정보 등록 성공")
                .build();
    }

    public RecipientListRecord getRecipient(HttpServletRequest request){
        Member member = getMember(request);

        List<Recipient> recipientList = member.getRecipientList();
        List<RecipientRecord> list = new LinkedList<>();

        for(Recipient r : recipientList){
            list.add(new RecipientRecord(r.getId(), r.getName(), r.getAddress(), r.getTel(), r.getStatus(), r.getCreatedAt(), r.getModifiedAt()));
        }
        Collections.reverse(list);
        return new RecipientListRecord(list);
    }
}
