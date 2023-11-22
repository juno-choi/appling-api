package com.juno.appling.order.domain.entity;

import com.juno.appling.order.domain.model.Delivery;
import com.juno.appling.order.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery")
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @Enumerated(EnumType.STRING)
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

    public static DeliveryEntity from(Delivery delivery) {
        if(delivery == null) {
            return null;
        }

        DeliveryEntity deliveryEntity = new DeliveryEntity();
        deliveryEntity.id = delivery.getId();
        deliveryEntity.status = delivery.getStatus();
        deliveryEntity.ownerName = delivery.getOwnerName();
        deliveryEntity.ownerZonecode = delivery.getOwnerZonecode();
        deliveryEntity.ownerAddress = delivery.getOwnerAddress();
        deliveryEntity.ownerAddressDetail = delivery.getOwnerAddressDetail();
        deliveryEntity.ownerTel = delivery.getOwnerTel();
        deliveryEntity.recipientName = delivery.getRecipientName();
        deliveryEntity.recipientZonecode = delivery.getRecipientZonecode();
        deliveryEntity.recipientAddress = delivery.getRecipientAddress();
        deliveryEntity.recipientAddressDetail = delivery.getRecipientAddressDetail();
        deliveryEntity.recipientTel = delivery.getRecipientTel();
        deliveryEntity.createdAt = delivery.getCreatedAt();
        deliveryEntity.modifiedAt = delivery.getModifiedAt();
        return deliveryEntity;
    }

    public Delivery toModel() {
        return Delivery.builder()
            .id(id)
            .status(status)
            .ownerName(ownerName)
            .ownerZonecode(ownerZonecode)
            .ownerAddress(ownerAddress)
            .ownerAddressDetail(ownerAddressDetail)
            .ownerTel(ownerTel)
            .recipientName(recipientName)
            .recipientZonecode(recipientZonecode)
            .recipientAddress(recipientAddress)
            .recipientAddressDetail(recipientAddressDetail)
            .recipientTel(recipientTel)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

}
