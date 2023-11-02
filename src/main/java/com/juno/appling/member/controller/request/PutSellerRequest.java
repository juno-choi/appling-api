package com.juno.appling.member.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PutSellerRequest {

    @NotNull(message = "company 비어있을 수 없습니다.")
    private String company;
    @NotNull(message = "tel 비어있을 수 없습니다.")
    @Column(length = 11)
    private String tel;
    @NotNull(message = "zonecode 비어있을 수 없습니다.")
    private String zonecode;
    @NotNull(message = "address 비어있을 수 없습니다.")
    private String address;
    @JsonProperty("address_detail")
    @NotNull(message = "address_detail 비어있을 수 없습니다.")
    private String addressDetail;
    @Email(message = "email 형식을 맞춰주세요.")
    private String email;
}
