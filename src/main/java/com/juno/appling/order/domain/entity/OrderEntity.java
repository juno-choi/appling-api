package com.juno.appling.order.domain.entity;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Audited
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ORDERS")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private String orderNumber;

    @NotAudited
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItemList = new ArrayList<>();

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private DeliveryEntity delivery;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String orderName;   // server에서 자동으로 생성

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderEntity from(Order order) {
        if(order == null) {
            return null;
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.id = order.getId();
        orderEntity.member = MemberEntity.from(
            Optional.ofNullable(
                order.getMember())
                .orElse(Member.builder().build()
            )
        );
        orderEntity.orderNumber = order.getOrderNumber();
        orderEntity.orderItemList = Optional.ofNullable(order.getOrderItemList()).orElse(new ArrayList<>()).stream().map(OrderItemEntity::from).collect(
            Collectors.toList());
        orderEntity.delivery = DeliveryEntity.from(order.getDelivery());
        orderEntity.status = order.getStatus();
        orderEntity.orderName = order.getOrderName();
        orderEntity.createdAt = order.getCreatedAt();
        orderEntity.modifiedAt = order.getModifiedAt();
        return orderEntity;
    }

    public Order toModel(){
        return Order.builder()
            .id(id)
            .member(member.toModel())
            .orderNumber(orderNumber)
            .orderItemList(orderItemList.stream().map(OrderItemEntity::toModel).collect(Collectors.toList()))
            .delivery(delivery == null ? null : delivery.toModel())
            .status(status)
            .orderName(orderName)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

    private OrderEntity(MemberEntity member, OrderStatus status, String orderName, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.member = member;
        this.status = status;
        this.orderName = orderName;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderEntity of(MemberEntity memberEntity, String orderName){
        LocalDateTime now = LocalDateTime.now();
        return new OrderEntity(memberEntity, OrderStatus.TEMP, orderName, now, now);
    }

    public void statusComplete() {
        this.status = OrderStatus.COMPLETE;
        this.modifiedAt = LocalDateTime.now();
    }

    public void orderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    public void addOrderItem(OrderItemEntity orderItemEntity) {
        this.orderItemList.add(orderItemEntity);
    }
}
