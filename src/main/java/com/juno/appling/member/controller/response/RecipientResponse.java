package com.juno.appling.member.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.entity.RecipientEntity;
import com.juno.appling.member.enums.RecipientInfoStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class RecipientResponse {
    private Long recipient_id;
    private String name;
    private String zonecode;
    private String address;
    private String addressDetail;
    private String tel;
    private RecipientInfoStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static RecipientResponse from(RecipientEntity recipientEntity) {
        return RecipientResponse.builder()
                .recipient_id(recipientEntity.getId())
                .name(recipientEntity.getName())
                .zonecode(recipientEntity.getZonecode())
                .address(recipientEntity.getAddress())
                .addressDetail(recipientEntity.getAddressDetail())
                .tel(recipientEntity.getTel())
                .status(recipientEntity.getStatus())
                .createdAt(recipientEntity.getCreatedAt())
                .modifiedAt(recipientEntity.getModifiedAt())
                .build();
    }
}
