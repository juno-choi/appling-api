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
import com.juno.appling.member.domain.Introduce;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.MemberApplySeller;
import com.juno.appling.member.domain.Recipient;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.enums.IntroduceStatus;
import com.juno.appling.member.enums.MemberApplySellerStatus;
import com.juno.appling.member.enums.RecipientInfoStatus;
import com.juno.appling.member.enums.Role;
import com.juno.appling.member.infrastruceture.IntroduceRepository;
import com.juno.appling.member.infrastruceture.MemberApplySellerRepository;
import com.juno.appling.member.infrastruceture.MemberRepository;
import com.juno.appling.member.infrastruceture.RecipientRepository;
import com.juno.appling.member.infrastruceture.SellerRepository;
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

    @Override
    public MemberResponse member(HttpServletRequest request) {
        Member findMember = getMember(request);

        return MemberResponse.from(findMember);
    }


    private void permitSeller(Member member, MemberApplySeller memberApplySeller) {
        memberApplySeller.patchApplyStatus(MemberApplySellerStatus.PERMIT);
        member.patchMemberRole(Role.SELLER);
    }

    @Transactional
    @Override
    public MessageVo patchMember(PatchMemberRequest patchMemberRequest, HttpServletRequest request) {
        Member member = getMember(request);

        String birth = Optional.ofNullable(patchMemberRequest.getBirth()).orElse("")
                .replaceAll("-", "")
                .trim();
        String name = Optional.ofNullable(patchMemberRequest.getName()).orElse("").trim();
        String nickname = Optional.ofNullable(patchMemberRequest.getNickname()).orElse("").trim();
        String password = Optional.ofNullable(patchMemberRequest.getPassword()).orElse("").trim();
        if (!password.isEmpty()) {
            password = passwordEncoder.encode(password);
        }
        member.patchMember(birth, name, nickname, password);

        return new MessageVo("회원 정보 수정 성공");
    }

    @Transactional
    @Override
    public MessageVo postRecipient(PostRecipientRequest postRecipientRequestInfo, HttpServletRequest request) {
        Member member = getMember(request);
        recipientRepository.save(
                Recipient.of(member, postRecipientRequestInfo.getName(),
                        postRecipientRequestInfo.getZonecode(),
                        postRecipientRequestInfo.getAddress(), postRecipientRequestInfo.getAddressDetail(),
                        postRecipientRequestInfo.getTel(),
                        RecipientInfoStatus.NORMAL));

        return new MessageVo("수령인 정보 등록 성공");
    }

    @Override
    public RecipientListResponse getRecipient(HttpServletRequest request) {
        Member member = getMember(request);

        List<Recipient> recipientList = member.getRecipientList();
        List<RecipientResponse> list = new LinkedList<>();

        recipientList.sort((r1, r2) -> {
            if (r1.getModifiedAt().isAfter(r2.getModifiedAt())) {
                return -1;
            }
            return 1;
        });

        if (!recipientList.isEmpty()) {
            Recipient r = recipientList.get(0);
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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        Optional<Seller> optionalSellerByCompany = sellerRepository.findByCompany(postSellerRequest.getCompany());
        if(optionalSellerByCompany.isPresent()){
            throw new IllegalArgumentException("이미 등록된 판매자명입니다.");
        }

        Optional<Seller> optionalSeller = sellerRepository.findByMember(member);
        if (optionalSeller.isPresent()) {
            throw new IllegalArgumentException("이미 판매자 신청을 완료했습니다.");
        }

        Seller seller = Seller.of(member, postSellerRequest.getCompany(),
                postSellerRequest.getTel(),
                postSellerRequest.getZonecode(), postSellerRequest.getAddress(), postSellerRequest.getAddressDetail(),
                postSellerRequest.getEmail());
        sellerRepository.save(seller);

        // 임시적 승인 절차
        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(
                MemberApplySeller.of(memberId));
        permitSeller(member, saveMemberApply);

        return new MessageVo("판매자 정보 등록 성공");
    }


    private Seller getSellerByRequest(HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        return sellerRepository.findByMember(member).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 판매자입니다. 판매자 신청을 먼저 해주세요.")
        );
    }

    @Transactional
    @Override
    public MessageVo putSeller(PutSellerRequest putSellerRequest, HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);
        seller.put(putSellerRequest);
        return new MessageVo("판매자 정보 수정 성공");
    }

    @Override
    public SellerResponse getSeller(HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);
        return SellerResponse.from(seller);
    }

    @Transactional
    @Override
    public MessageVo postIntroduce(PostIntroduceRequest postIntroduceRequest, HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);
        Optional<Introduce> introduceFindBySeller = introduceRepository.findBySeller(seller);

        if (introduceFindBySeller.isEmpty()) {
            introduceRepository.save(
                    Introduce.of(seller, postIntroduceRequest.getSubject(),
                            postIntroduceRequest.getUrl(),
                            IntroduceStatus.USE));
        } else {
            Introduce introduce = introduceFindBySeller.get();
            introduce.changeUrl(introduce.getUrl());
        }

        return new MessageVo("소개글 등록 성공");
    }

    @Override
    public String getIntroduce(HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);
        return getSellerIntroduceHtml(seller);
    }

    @Override
    public String getIntroduce(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 판매자입니다."));
        return getSellerIntroduceHtml(seller);
    }

    private String getSellerIntroduceHtml(Seller seller) {
        Introduce introduce = introduceRepository.findBySeller(seller)
                .orElseThrow(() -> new IllegalArgumentException("소개 페이지를 먼저 등록해주세요."));

        String url = introduce.getUrl();
        url = url.substring(url.indexOf("s3.ap-northeast-2.amazonaws.com")
                + "s3.ap-northeast-2.amazonaws.com".length() + 1);
        String bucket = env.getProperty("cloud.s3.bucket");

        return s3Service.getObject(url, bucket);
    }
}
