package com.juno.appling.service.member;

import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.vo.member.JoinVo;
import com.juno.appling.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public JoinVo join(JoinDto joinDto){

        Member saveMember = memberRepository.save(Member.createMember(joinDto));
        return JoinVo.builder()
                .email(saveMember.getEmail())
                .name(saveMember.getName())
                .nickname(saveMember.getNickname())
                .build();
    }
}
