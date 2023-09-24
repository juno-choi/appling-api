package com.juno.appling.order.domain;

import com.juno.appling.member.domain.Seller;
import jakarta.persistence.*;

@Entity
public class OrderSellerList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_seller_list_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private OrderSellerList(Order order, Seller seller) {
        this.order = order;
        this.seller = seller;
    }

    public static OrderSellerList of(Order order, Seller seller) {
        return new OrderSellerList(order, seller);
    }
}
