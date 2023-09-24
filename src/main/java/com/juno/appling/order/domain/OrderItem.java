package com.juno.appling.order.domain;

import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Audited
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @NotAudited
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotAudited
    private Product product;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private int ea;

    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private OrderItem(Order order, Product product, OrderItemStatus status, int ea,
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

    public static OrderItem of(Order order, Product product, int amount) {
        LocalDateTime now = LocalDateTime.now();
        return new OrderItem(order, product, OrderItemStatus.TEMP, amount, product.getPrice(),
            product.getPrice() * amount, now, now);
    }
}
