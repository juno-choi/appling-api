package com.juno.appling.service.member;

import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.vo.member.JoinVo;
import com.juno.appling.repository.member.MemberRepository;
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
    private final BCryptPasswordEncoder passwordEncoder;
    @Transactional
    public JoinVo join(JoinDto joinDto){
        joinDto.passwordEncoder(passwordEncoder);
        Optional<Member> findMember = memberRepository.findByEmail(joinDto.getEmail());
        if(findMember.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member saveMember = memberRepository.save(Member.createMember(joinDto));
        return JoinVo.builder()
                .email(saveMember.getEmail())
                .name(saveMember.getName())
                .nickname(saveMember.getNickname())
                .build();
    }
}
