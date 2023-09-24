package com.juno.appling.order.domain;

import com.juno.appling.member.domain.Member;
import com.juno.appling.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Audited
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotAudited
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @NotAudited
    @OneToMany(mappedBy = "order")
    private List<Delivery> deliveryList = new ArrayList<>();

    @NotAudited
    @OneToMany(mappedBy = "order")
    private List<OrderList> sellerList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String orderName;   // server에서 자동으로 생성

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
