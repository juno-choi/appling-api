package com.juno.appling.order.domain.model;

import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.domain.model.Product;
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
    private OrderItemStatus status;
    private int ea;
    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
