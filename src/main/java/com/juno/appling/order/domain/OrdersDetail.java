package com.juno.appling.order.domain;

import com.juno.appling.order.enums.OrdersDetailStatus;
import com.juno.appling.product.domain.Product;
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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Getter
@NoArgsConstructor
@Audited
public class OrdersDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @NotAudited
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotAudited
    private Product product;

    @Enumerated(EnumType.STRING)
    private OrdersDetailStatus status;

    private int ea;

    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private OrdersDetail(Orders order, Product product, OrdersDetailStatus status, int ea,
        int productPrice, int productTotalPrice, LocalDateTime createdAt,
        LocalDateTime modifiedAt) {
        this.order = order;
        this.product = product;
        this.status = status;
        this.ea = ea;
        this.productPrice = productPrice;
        this.productTotalPrice = productTotalPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrdersDetail of(Orders order, Product product, int ea) {
        LocalDateTime now = LocalDateTime.now();
        return new OrdersDetail(order, product, OrdersDetailStatus.TEMP, ea, product.getPrice(),
            product.getPrice() * ea, now, now);
    }
}
