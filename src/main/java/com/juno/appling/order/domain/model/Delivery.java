package com.juno.appling.order.domain.model;

import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.enums.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Delivery {
    private Long id;
    private Order order;
    private OrderItem orderItem;
    private DeliveryStatus status;

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

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Delivery create(CompleteOrderRequest completeOrderRequest, OrderItem orderItem, Order order) {
        return Delivery.builder()
                .order(order)
                .orderItem(orderItem)
                .status(DeliveryStatus.TEMP)
                .ownerName(completeOrderRequest.getOwnerName())
                .ownerZonecode(completeOrderRequest.getOwnerZonecode())
                .ownerAddress(completeOrderRequest.getOwnerAddress())
                .ownerAddressDetail(completeOrderRequest.getOwnerAddressDetail())
                .ownerTel(completeOrderRequest.getOwnerTel())
                .recipientName(completeOrderRequest.getRecipientName())
                .recipientZonecode(completeOrderRequest.getRecipientZonecode())
                .recipientAddress(completeOrderRequest.getRecipientAddress())
                .recipientAddressDetail(completeOrderRequest.getRecipientAddressDetail())
                .recipientTel(completeOrderRequest.getRecipientTel())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
