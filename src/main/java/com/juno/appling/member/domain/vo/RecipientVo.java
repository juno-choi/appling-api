package com.juno.appling.member.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.enums.RecipientInfoStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecipientVo(
    Long id,
    String name,
    String zonecode,
    String address,
    String tel,
    RecipientInfoStatus status,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {

}
