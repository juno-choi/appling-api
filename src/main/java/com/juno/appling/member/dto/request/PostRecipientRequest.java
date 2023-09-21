package com.juno.appling.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRecipientRequest {

    @NotNull(message = "name 비어있을 수 없습니다.")
    private String name;
    @NotNull(message = "zonecode 비어있을 수 없습니다.")
    private String zonecode;
    @NotNull(message = "address 비어있을 수 없습니다.")
    private String address;

    @NotNull(message = "address_detail 비어있을 수 없습니다.")
    @JsonProperty("address_detail")
    private String addressDetail;
    @NotNull(message = "tel 비어있을 수 없습니다.")
    private String tel;
}
