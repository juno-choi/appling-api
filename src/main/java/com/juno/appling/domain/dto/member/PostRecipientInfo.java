package com.juno.appling.domain.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRecipientInfo {
    @NotNull(message = "name 비어있을 수 없습니다.")
    private String name;
    @NotNull(message = "address 비어있을 수 없습니다.")
    private String address;
    @NotNull(message = "tel 비어있을 수 없습니다.")
    private String tel;
}
