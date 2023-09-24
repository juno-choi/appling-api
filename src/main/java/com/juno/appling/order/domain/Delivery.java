package com.juno.appling.order.domain;

import com.juno.appling.order.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

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
}
