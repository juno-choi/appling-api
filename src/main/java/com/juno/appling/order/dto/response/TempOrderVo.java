package com.juno.appling.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.OrderItem;
import com.juno.appling.product.enums.Status;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TempOrderVo {

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
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private SellerVo seller;
    private CategoryVo category;

    private TempOrderVo(Long productId, int ea, String mainTitle, String mainExplanation,
        String productMainExplanation, String productSubExplanation, int originPrice, int price,
        String purchaseInquiry, String origin, String producer, String mainImage, String image1,
        String image2, String image3, Long viewCnt, Status status, LocalDateTime createdAt,
        LocalDateTime modifiedAt, SellerVo seller, CategoryVo category) {
        this.productId = productId;
        this.ea = ea;
        this.mainTitle = mainTitle;
        this.mainExplanation = mainExplanation;
        this.productMainExplanation = productMainExplanation;
        this.productSubExplanation = productSubExplanation;
        this.originPrice = originPrice;
        this.price = price;
        this.purchaseInquiry = purchaseInquiry;
        this.origin = origin;
        this.producer = producer;
        this.mainImage = mainImage;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.viewCnt = viewCnt;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.seller = seller;
        this.category = category;
    }

    public static TempOrderVo of(OrderItem orderItem) {
        return new TempOrderVo(orderItem.getProduct().getId(), orderItem.getEa(),
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
            CategoryVo.of(orderItem.getProduct().getCategory()));
    }
}
