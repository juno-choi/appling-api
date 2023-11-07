package com.juno.appling.order.domain.model;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.model.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Order {
    private Long id;
    private Member member;
    private String orderNumber;
    private List<OrderItem> orderItemList;
    private List<Delivery> deliveryList;
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
}
