package com.juno.appling.order.domain.model;

import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.enums.ProductType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItem {
    private Long id;
    private Order order;
    // TODO OrderItem에서 product, option 자체를 분리해야할지 고민해봐야 함
    private Product product;
    private Option option;
    private OrderProduct orderProduct;
    private OrderOption orderOption;
    private OrderItemStatus status;
    private int ea;
    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderItem create(Order order, Product product, TempOrderDto tempOrderDto) {
        ProductType type = product.getType();
        OrderItem orderItem = null;
        if(type == ProductType.NORMAL) {
            int ea = tempOrderDto.getEa();

            orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .orderProduct(OrderProduct.create(product))
                .status(OrderItemStatus.TEMP)
                .ea(ea)
                .productPrice(product.getPrice())
                .productTotalPrice(product.getPrice() * ea)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        }else {
            orderItem = OrderItem.builder()
                .build();
        }

        return orderItem;
    }
}
