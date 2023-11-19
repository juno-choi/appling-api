package com.juno.appling.order.domain.model;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.model.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    public void complete() {
        this.status = OrderStatus.COMPLETE;
    }

    public void cancel() {
        this.status = OrderStatus.CANCEL;
    }
}
