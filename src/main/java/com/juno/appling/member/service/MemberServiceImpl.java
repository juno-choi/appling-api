package com.juno.appling.member.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.s3.S3Service;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.controller.request.PatchMemberRequest;
import com.juno.appling.member.controller.request.PostIntroduceRequest;
import com.juno.appling.member.controller.request.PostRecipientRequest;
import com.juno.appling.member.controller.request.PostSellerRequest;
import com.juno.appling.member.controller.request.PutSellerRequest;
import com.juno.appling.member.controller.response.MemberResponse;
import com.juno.appling.member.controller.response.RecipientListResponse;
import com.juno.appling.member.controller.response.RecipientResponse;
import com.juno.appling.member.enums.MemberRole;
import com.juno.appling.product.domain.entity.IntroduceEntity;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.entity.MemberApplySellerEntity;
import com.juno.appling.member.domain.entity.RecipientEntity;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.member.enums.IntroduceStatus;
import com.juno.appling.member.enums.MemberApplySellerStatus;
import com.juno.appling.member.enums.RecipientInfoStatus;
import com.juno.appling.member.port.IntroduceJpaRepository;
import com.juno.appling.member.port.MemberApplySellerJpaRepository;
import com.juno.appling.member.port.MemberJpaRepository;
import com.juno.appling.member.port.RecipientJpaRepository;
import com.juno.appling.product.port.SellerJpaRepository;
import com.juno.appling.product.controller.response.SellerResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{

    private final MemberJpaRepository memberJpaRepository;
    private final MemberApplySellerJpaRepository memberApplySellerJpaRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RecipientJpaRepository recipientJpaRepository;
    private final SellerJpaRepository sellerJpaRepository;
    private final IntroduceJpaRepository introduceJpaRepository;
    private final Environment env;
    private final S3Service s3Service;

    private MemberEntity getMember(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        return memberJpaRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 회원입니다.")
        );
    }

    @Override
    public MemberResponse member(HttpServletRequest request) {
        MemberEntity findMemberEntity = getMember(request);

        return MemberResponse.from(findMemberEntity);
    }


    private void permitSeller(MemberEntity memberEntity, MemberApplySellerEntity memberApplySellerEntity) {
        memberApplySellerEntity.patchApplyStatus(MemberApplySellerStatus.PERMIT);
        memberEntity.patchMemberRole(MemberRole.SELLER);
    }

    @Transactional
    @Override
    public MessageVo patchMember(PatchMemberRequest patchMemberRequest, HttpServletRequest request) {
        MemberEntity memberEntity = getMember(request);

        String birth = Optional.ofNullable(patchMemberRequest.getBirth()).orElse("")
                .replaceAll("-", "")
                .trim();
        String name = Optional.ofNullable(patchMemberRequest.getName()).orElse("").trim();
        String nickname = Optional.ofNullable(patchMemberRequest.getNickname()).orElse("").trim();
        String password = Optional.ofNullable(patchMemberRequest.getPassword()).orElse("").trim();
        if (!password.isEmpty()) {
            password = passwordEncoder.encode(password);
        }
        memberEntity.patchMember(birth, name, nickname, password);

        return new MessageVo("회원 정보 수정 성공");
    }

    @Transactional
    @Override
    public MessageVo postRecipient(PostRecipientRequest postRecipientRequestInfo, HttpServletRequest request) {
        MemberEntity memberEntity = getMember(request);
        recipientJpaRepository.save(
                RecipientEntity.of(memberEntity, postRecipientRequestInfo.getName(),
                        postRecipientRequestInfo.getZonecode(),
                        postRecipientRequestInfo.getAddress(), postRecipientRequestInfo.getAddressDetail(),
                        postRecipientRequestInfo.getTel(),
                        RecipientInfoStatus.NORMAL));

        return new MessageVo("수령인 정보 등록 성공");
    }

    @Override
    public RecipientListResponse getRecipient(HttpServletRequest request) {
        MemberEntity memberEntity = getMember(request);

        List<RecipientEntity> recipientEntityList = memberEntity.getRecipientList();
        List<RecipientResponse> list = new LinkedList<>();

        recipientEntityList.sort((r1, r2) -> {
            if (r1.getModifiedAt().isAfter(r2.getModifiedAt())) {
                return -1;
            }
            return 1;
        });

        if (!recipientEntityList.isEmpty()) {
            RecipientEntity r = recipientEntityList.get(0);
            list.add(
                    RecipientResponse.from(r)
            );
        }

        return RecipientListResponse.builder().list(list).build();
    }

    @Transactional
    @Override
    public MessageVo postSeller(PostSellerRequest postSellerRequest, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        MemberEntity memberEntity = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        Optional<SellerEntity> optionalSellerByCompany = sellerJpaRepository.findByCompany(postSellerRequest.getCompany());
        if(optionalSellerByCompany.isPresent()){
            throw new IllegalArgumentException("이미 등록된 판매자명입니다.");
        }

        Optional<SellerEntity> optionalSeller = sellerJpaRepository.findByMember(memberEntity);
        if (optionalSeller.isPresent()) {
            throw new IllegalArgumentException("이미 판매자 신청을 완료했습니다.");
        }

        SellerEntity sellerEntity = SellerEntity.of(memberEntity, postSellerRequest.getCompany(),
                postSellerRequest.getTel(),
                postSellerRequest.getZonecode(), postSellerRequest.getAddress(), postSellerRequest.getAddressDetail(),
                postSellerRequest.getEmail());
        sellerJpaRepository.save(sellerEntity);

        // 임시적 승인 절차
        MemberApplySellerEntity saveMemberApply = memberApplySellerJpaRepository.save(
                MemberApplySellerEntity.of(memberId));
        permitSeller(memberEntity, saveMemberApply);

        return new MessageVo("판매자 정보 등록 성공");
    }


    private SellerEntity getSellerByRequest(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        MemberEntity memberEntity = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        return sellerJpaRepository.findByMember(memberEntity).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 판매자입니다. 판매자 신청을 먼저 해주세요.")
        );
    }

    @Transactional
    @Override
    public MessageVo putSeller(PutSellerRequest putSellerRequest, HttpServletRequest request) {
        SellerEntity sellerEntity = getSellerByRequest(request);
        sellerEntity.put(putSellerRequest);
        return new MessageVo("판매자 정보 수정 성공");
    }

    @Override
    public SellerResponse getSeller(HttpServletRequest request) {
        SellerEntity sellerEntity = getSellerByRequest(request);
        return SellerResponse.from(sellerEntity);
    }

    @Transactional
    @Override
    public MessageVo postIntroduce(PostIntroduceRequest postIntroduceRequest, HttpServletRequest request) {
        SellerEntity sellerEntity = getSellerByRequest(request);
        Optional<IntroduceEntity> introduceFindBySeller = introduceJpaRepository.findBySeller(
            sellerEntity);

        if (introduceFindBySeller.isEmpty()) {
            introduceJpaRepository.save(
                    IntroduceEntity.of(sellerEntity, postIntroduceRequest.getSubject(),
                            postIntroduceRequest.getUrl(),
                            IntroduceStatus.USE));
        } else {
            IntroduceEntity introduceEntity = introduceFindBySeller.get();
            introduceEntity.changeUrl(introduceEntity.getUrl());
        }

        return new MessageVo("소개글 등록 성공");
    }

    @Override
    public String getIntroduce(HttpServletRequest request) {
        SellerEntity sellerEntity = getSellerByRequest(request);
        return getSellerIntroduceHtml(sellerEntity);
    }

    @Override
    public String getIntroduce(Long sellerId) {
        SellerEntity sellerEntity = sellerJpaRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 판매자입니다."));
        return getSellerIntroduceHtml(sellerEntity);
    }

    private String getSellerIntroduceHtml(SellerEntity sellerEntity) {
        IntroduceEntity introduceEntity = introduceJpaRepository.findBySeller(sellerEntity)
                .orElseThrow(() -> new IllegalArgumentException("소개 페이지를 먼저 등록해주세요."));

        String url = introduceEntity.getUrl();
        url = url.substring(url.indexOf("s3.ap-northeast-2.amazonaws.com")
                + "s3.ap-northeast-2.amazonaws.com".length() + 1);
        String bucket = env.getProperty("cloud.s3.bucket");

        return s3Service.getObject(url, bucket);
    }
}
