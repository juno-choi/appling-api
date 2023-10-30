package com.juno.appling.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.Delivery;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeliveryVo {
    private String ownerName;
    private String ownerZonecode;
    private String ownerAddress;
    private String ownerAddressDetail;
    private String ownerTel;

    private String recipientName;
    private String recipientZonecode;
    private String recipientAddress;
    private String recipientAddressDetail;
    private String recipientTel;

    public static DeliveryVo fromDeliveryEntity(Delivery delivery) {
        return DeliveryVo.builder()
            .ownerName(delivery.getOwnerName())
            .ownerZonecode(delivery.getOwnerZonecode())
            .ownerAddress(delivery.getOwnerAddress())
            .ownerAddressDetail(delivery.getOwnerAddressDetail())
            .ownerTel(delivery.getOwnerTel())
            .recipientName(delivery.getRecipientName())
            .recipientZonecode(delivery.getRecipientZonecode())
            .recipientAddress(delivery.getRecipientAddress())
            .recipientAddressDetail(delivery.getRecipientAddressDetail())
            .recipientTel(delivery.getRecipientTel())
            .build();
    }
}
