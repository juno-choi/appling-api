package com.juno.appling.order.domain;

import com.juno.appling.member.domain.Seller;
import jakarta.persistence.*;

@Entity
public class OrderList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_list_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
