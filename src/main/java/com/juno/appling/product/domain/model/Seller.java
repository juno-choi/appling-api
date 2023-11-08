package com.juno.appling.product.domain.model;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.product.controller.response.SellerResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Seller {
    private Long id;
    private Member member;
    private String company;
    private String tel;
    private String zonecode;
    private String address;
    private String addressDetail;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public SellerResponse toResponse() {
        return SellerResponse.builder()
            .sellerId(id)
            .company(company)
            .tel(tel)
            .zonecode(zonecode)
            .address(address)
            .addressDetail(addressDetail)
            .email(email)
            .build();
    }
}
