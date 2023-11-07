package com.juno.appling.order.domain.model;

import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.enums.ProductType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderItem {
    private Long id;
    private Order order;
    private OrderProduct orderProduct;
    private OrderOption orderOption;
    private OrderItemStatus status;
    private int ea;
    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderItem create(Order order, OrderProduct orderProduct, OrderOption orderOption, int ea) {
        int price = orderProduct.getType() == ProductType.OPTION ? orderProduct.getPrice() + orderOption.getExtraPrice() : orderProduct.getPrice();
        int totalPrice = price * ea;

        return OrderItem.builder()
                .order(order)
                .orderProduct(orderProduct)
                .orderOption(orderOption)
                .status(OrderItemStatus.TEMP)
                .ea(ea)
                .productPrice(price)
                .productTotalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
