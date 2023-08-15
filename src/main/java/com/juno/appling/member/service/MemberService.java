package com.juno.appling.member.service;

import com.juno.appling.config.base.MessageVo;
import com.juno.appling.config.s3.S3Service;
import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.member.domain.dto.*;
import com.juno.appling.member.domain.entity.*;
import com.juno.appling.member.domain.enums.IntroduceStatus;
import com.juno.appling.member.domain.enums.MemberApplySellerStatus;
import com.juno.appling.member.domain.enums.RecipientInfoStatus;
import com.juno.appling.member.domain.enums.Role;
import com.juno.appling.member.domain.vo.MemberVo;
import com.juno.appling.member.domain.vo.RecipientListVo;
import com.juno.appling.member.domain.vo.RecipientVo;
import com.juno.appling.member.repository.*;
import com.juno.appling.product.domain.vo.SellerVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RecipientRepository recipientRepository;
    private final SellerRepository sellerRepository;
    private final IntroduceRepository introduceRepository;
    private final Environment env;
    private final S3Service s3Service;

    private Member getMember(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        return memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 회원입니다.")
        );
    }

    public MemberVo member(HttpServletRequest request) {
        Member findMember = getMember(request);

        return new MemberVo(findMember.getId(), findMember.getEmail(), findMember.getNickname(), findMember.getName(), findMember.getRole(), findMember.getSnsType(), findMember.getStatus(), findMember.getCreatedAt(), findMember.getModifiedAt());
    }


    private void permitSeller(Member member, MemberApplySeller memberApplySeller) {
        memberApplySeller.patchApplyStatus(MemberApplySellerStatus.PERMIT);
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

        return new MessageVo("회원 정보 수정 성공");
    }

    @Transactional
    public MessageVo postRecipient(PostRecipientDto postRecipientDtoInfo, HttpServletRequest request){
        Member member = getMember(request);
        recipientRepository.save(Recipient.of(member, postRecipientDtoInfo.getName(), postRecipientDtoInfo.getAddress(), postRecipientDtoInfo.getTel(), RecipientInfoStatus.NORMAL));
        return new MessageVo("수령인 정보 등록 성공");
    }

    public RecipientListVo getRecipient(HttpServletRequest request){
        Member member = getMember(request);

        List<Recipient> recipientList = member.getRecipientList();
        List<RecipientVo> list = new LinkedList<>();

        for(Recipient r : recipientList){
            list.add(new RecipientVo(r.getId(), r.getName(), r.getAddress(), r.getTel(), r.getStatus(), r.getCreatedAt(), r.getModifiedAt()));
        }
        Collections.reverse(list);
        return new RecipientListVo(list);
    }

    @Transactional
    public MessageVo postSeller(PostSellerDto postSellerDto, HttpServletRequest request){
        Long memberId = tokenProvider.getMemberId(request);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        Optional<Seller> optionalSeller = sellerRepository.findByMember(member);
        if(optionalSeller.isPresent()){
            throw new IllegalArgumentException("이미 판매자 신청을 완료했습니다.");
        }


        Seller seller = Seller.of(member, postSellerDto.getCompany(), postSellerDto.getTel(), postSellerDto.getAddress(), postSellerDto.getEmail());
        sellerRepository.save(seller);

        // 임시적 승인 절차
        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(MemberApplySeller.of(memberId));
        permitSeller(member, saveMemberApply);

        return new MessageVo("판매자 정보 등록 성공");
    }


    private Seller getSellerByRequest(HttpServletRequest request){
        Long memberId = tokenProvider.getMemberId(request);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        return sellerRepository.findByMember(member).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 판매자입니다. 판매자 신청을 먼저 해주세요.")
        );
    }

    @Transactional
    public MessageVo putSeller(PutSellerDto putSellerDto, HttpServletRequest request){
        Seller seller = getSellerByRequest(request);
        seller.put(putSellerDto);
        return new MessageVo("판매자 정보 수정 성공");
    }

    public SellerVo getSeller(HttpServletRequest request){
        Seller seller = getSellerByRequest(request);
        return new SellerVo(seller.getId(), seller.getEmail(), seller.getCompany(), seller.getAddress(), seller.getTel());
    }

    @Transactional
    public MessageVo postIntroduce(PostIntroduceDto postIntroduceDto, HttpServletRequest request){
        Seller seller = getSellerByRequest(request);
        introduceRepository.save(Introduce.of(seller, postIntroduceDto.getSubject(), postIntroduceDto.getUrl(), IntroduceStatus.USE));
        return new MessageVo("소개글 등록 성공");
    }

    public String getIntroduce(HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);
        Introduce introduce = introduceRepository.findBySeller(seller).orElseThrow(() -> new IllegalArgumentException("소개 페이지를 먼저 등록해주세요."));

        String url = introduce.getUrl();
        url = url.substring(url.indexOf("s3.ap-northeast-2.amazonaws.com") + "s3.ap-northeast-2.amazonaws.com".length());
        String bucket = env.getProperty("cloud.s3.bucket");


        return s3Service.getObject(url, bucket);
    }
}
