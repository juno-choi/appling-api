package com.juno.appling.order.domain.entity;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

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
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<DeliveryEntity> deliveryList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String orderName;   // server에서 자동으로 생성

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderEntity from(Order order) {
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
        orderEntity.deliveryList = Optional.ofNullable(order.getDeliveryList()).orElse(new ArrayList<>()).stream().map(DeliveryEntity::from).collect(
            Collectors.toList());
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
            .deliveryList(deliveryList.stream().map(DeliveryEntity::toModel).collect(Collectors.toList()))
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
