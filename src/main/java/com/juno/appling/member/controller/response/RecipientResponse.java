package com.juno.appling.member.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.Recipient;
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

    public static RecipientResponse from(Recipient recipient) {
        return RecipientResponse.builder()
                .recipient_id(recipient.getId())
                .name(recipient.getName())
                .zonecode(recipient.getZonecode())
                .address(recipient.getAddress())
                .addressDetail(recipient.getAddressDetail())
                .tel(recipient.getTel())
                .status(recipient.getStatus())
                .createdAt(recipient.getCreatedAt())
                .modifiedAt(recipient.getModifiedAt())
                .build();
    }
}
