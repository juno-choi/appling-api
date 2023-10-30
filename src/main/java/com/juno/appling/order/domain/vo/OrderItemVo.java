package com.juno.appling.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.OrderItem;
import com.juno.appling.product.domain.vo.OptionVo;
import com.juno.appling.product.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
public class OrderItemVo {

    private Long productId;
    private int ea;
    private String mainTitle;
    private String mainExplanation;
    private String productMainExplanation;
    private String productSubExplanation;
    private int originPrice;
    private int price;
    private String purchaseInquiry;
    private String origin;
    private String producer;
    private String mainImage;
    private String image1;
    private String image2;
    private String image3;
    private Long viewCnt;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private SellerVo seller;
    private CategoryVo category;
    private OptionVo option;

    public static OrderItemVo fromOrderItemEntity(OrderItem orderItem) {
        return new OrderItemVo(orderItem.getProduct().getId(), orderItem.getEa(),
            orderItem.getProduct().getMainTitle(),
            orderItem.getProduct().getMainExplanation(),
            orderItem.getProduct().getProductMainExplanation(),
            orderItem.getProduct().getProductSubExplanation(),
            orderItem.getProduct().getOriginPrice(), orderItem.getProduct().getPrice(),
            orderItem.getProduct().getPurchaseInquiry(),
            orderItem.getProduct().getOrigin(), orderItem.getProduct().getProducer(),
            orderItem.getProduct().getMainImage(), orderItem.getProduct().getImage1(),
            orderItem.getProduct().getImage2(), orderItem.getProduct().getImage3(),
            orderItem.getProduct().getViewCnt(), orderItem.getProduct().getStatus(),
            orderItem.getProduct().getCreateAt(), orderItem.getModifiedAt(),
            SellerVo.of(orderItem.getProduct().getSeller()),
            CategoryVo.of(orderItem.getProduct().getCategory()),
            OptionVo.of(orderItem.getOption())
        );

    }
}
