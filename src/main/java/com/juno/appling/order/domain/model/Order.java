package com.juno.appling.order.domain.model;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.controller.response.OrderResponse;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.model.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class Order {
    private Long id;
    private Member member;
    private String orderNumber;
    private List<OrderItem> orderItemList;
    private Delivery delivery;
    private OrderStatus status;
    private String orderName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Order create(Member member, List<Product> productList) {
        int productListSize = productList.size();
        StringBuilder orderNameBuilder = new StringBuilder();
        orderNameBuilder.append(productList.get(0).getMainTitle());
        String productExtraName = productListSize > 1 ? " 외 " + (productListSize - 1) + "개" : "";
        orderNameBuilder.append(productExtraName);

        return Order.builder()
                .member(member)
                .orderItemList(new ArrayList<>())
                .status(OrderStatus.TEMP)
                .orderName(orderNameBuilder.toString())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }


    public void createOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        StringBuilder orderNumberBuilder = new StringBuilder();
        orderNumberBuilder.append("ORDER");
        orderNumberBuilder.append("-");
        orderNumberBuilder.append(now.format(formatter));
        orderNumberBuilder.append("-");
        orderNumberBuilder.append(id);

        this.orderNumber = orderNumberBuilder.toString();
    }

    public void checkOrder(Member member) {
        if (!this.member.getId().equals(member.getId())) {
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }
    }

    public void ordered() {
        if(this.status != OrderStatus.TEMP){
            throw new IllegalArgumentException("임시 주문만 주문상태로 변경이 가능합니다.");
        }
        this.status = OrderStatus.ORDERED;
    }

    public void processing() {
        if(this.status != OrderStatus.ORDERED){
            throw new IllegalArgumentException("주문 완료만 상품준비중으로 변경이 가능합니다.");
        }
        this.status = OrderStatus.PROCESSING;
    }

    public void confirm() {
        if(this.status != OrderStatus.PROCESSING){
            throw new IllegalArgumentException("상품 준비중만 발송/배송완료으로 변경이 가능합니다.");
        }
        this.status = OrderStatus.CONFIRM;
    }

    public void cancel() {
        if(this.status != OrderStatus.ORDERED){
            throw new IllegalArgumentException("주문이 이미 진행되었습니다.");
        }
        this.status = OrderStatus.CANCEL;
    }

    public OrderResponse toResponse() {
        return OrderResponse.builder()
                .orderId(id)
                .member(member.toResponseForOthers())
                .orderNumber(orderNumber)
                .orderItemList(orderItemList.stream().map(OrderItem::toResponse).collect(Collectors.toList()))
                .delivery(delivery == null ? null : delivery.toResponse())
                .status(status)
                .totalPrice(orderItemList.stream().mapToInt(OrderItem::getProductTotalPrice).sum())
                .orderName(orderName)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();
    }

    public void delivery(Delivery delivery) {
        this.delivery = delivery;
    }
}
