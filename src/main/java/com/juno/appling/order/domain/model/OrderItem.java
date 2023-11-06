package com.juno.appling.order.domain.model;

import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.enums.ProductType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
        OrderItem orderItem;
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
            Option option = product.getOptionList().stream().filter(
                    o -> o.getId().equals(tempOrderDto.getOptionId())
            ).findFirst().orElseThrow(
                () -> new IllegalArgumentException(
                    "유효하지 않은 옵션입니다. option id = " + tempOrderDto.getOptionId()
                )
            );

            OrderOption orderOption = OrderOption.create(option, order);
            int optionPrice = product.getPrice() + option.getExtraPrice();
            orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .orderProduct(OrderProduct.create(product))
                .option(option)
                .orderOption(orderOption)
                .status(OrderItemStatus.TEMP)
                .ea(tempOrderDto.getEa())
                .productPrice(optionPrice)
                .productTotalPrice(optionPrice * tempOrderDto.getEa())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        }

        return orderItem;
    }
}
