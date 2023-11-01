package com.juno.appling.member.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.controller.request.PatchMemberRequest;
import com.juno.appling.member.controller.request.PostIntroduceRequest;
import com.juno.appling.member.controller.request.PostRecipientRequest;
import com.juno.appling.member.controller.request.PostSellerRequest;
import com.juno.appling.member.controller.request.PutSellerRequest;
import com.juno.appling.member.controller.response.MemberResponse;
import com.juno.appling.member.controller.response.RecipientListResponse;
import com.juno.appling.product.controller.response.SellerResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberService {

    MemberResponse member(HttpServletRequest request);
    MessageVo patchMember(PatchMemberRequest patchMemberRequest, HttpServletRequest request);
    MessageVo postRecipient(PostRecipientRequest postRecipientRequestInfo, HttpServletRequest request);
    RecipientListResponse getRecipient(HttpServletRequest request);
    MessageVo postSeller(PostSellerRequest postSellerRequest, HttpServletRequest request);

    MessageVo putSeller(PutSellerRequest putSellerRequest, HttpServletRequest request);
    SellerResponse getSeller(HttpServletRequest request);
    MessageVo postIntroduce(PostIntroduceRequest postIntroduceRequest, HttpServletRequest request);
    String getIntroduce(HttpServletRequest request);
    String getIntroduce(Long sellerId);
}
