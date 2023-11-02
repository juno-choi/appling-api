package com.juno.appling.order.domain.entity;

import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.domain.entity.OptionEntity;
import com.juno.appling.product.domain.entity.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Getter
@NoArgsConstructor
@Audited
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @NotAudited
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotAudited
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    @NotAudited
    private OptionEntity option;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private int ea;

    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private OrderItemEntity(OrderEntity order, ProductEntity product, OptionEntity option, OrderItemStatus status, int ea,
                      int productPrice, int productTotalPrice, LocalDateTime createdAt,
                      LocalDateTime modifiedAt) {
        this.order = order;
        this.product = product;
        this.option = option;
        this.status = status;
        this.ea = ea;
        this.productPrice = productPrice;
        this.productTotalPrice = productTotalPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderItemEntity of(OrderEntity orderEntity, ProductEntity productEntity, OptionEntity optionEntity, int ea) {
        LocalDateTime now = LocalDateTime.now();
        return new OrderItemEntity(orderEntity, productEntity, optionEntity, OrderItemStatus.TEMP, ea, productEntity.getPrice(),
            productEntity.getPrice() * ea, now, now);
    }
}
