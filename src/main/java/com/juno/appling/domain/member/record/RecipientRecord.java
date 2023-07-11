package com.juno.appling.domain.member.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.domain.member.enums.RecipientInfoStatus;
import com.juno.appling.config.base.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecipientRecord(Long id, String name, String address, String tel, RecipientInfoStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
}
