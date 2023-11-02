package com.juno.appling.product.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.Seller;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class SellerResponse {
    @NotNull
    private Long sellerId;
    @NotNull
    private String email;
    @NotNull
    private String company;
    @NotNull
    private String zonecode;
    @NotNull
    private String address;
    @NotNull
    private String addressDetail;
    @NotNull
    String tel;

    public static SellerResponse from(Seller seller) {
        return SellerResponse.builder()
            .sellerId(seller.getId())
            .email(seller.getEmail())
            .company(seller.getCompany())
            .zonecode(seller.getZonecode())
            .address(seller.getAddress())
            .addressDetail(seller.getAddressDetail())
            .tel(seller.getTel())
            .build();
    }
}
