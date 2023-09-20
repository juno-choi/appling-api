package com.juno.appling.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.OrdersDetail;
import com.juno.appling.product.domain.Product;
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

    public static TempOrderVo of(OrdersDetail ordersDetail) {
        return new TempOrderVo(ordersDetail.getProduct().getId(), ordersDetail.getEa(),
            ordersDetail.getProduct().getMainTitle(),
            ordersDetail.getProduct().getMainExplanation(),
            ordersDetail.getProduct().getProductMainExplanation(),
            ordersDetail.getProduct().getProductSubExplanation(),
            ordersDetail.getProduct().getOriginPrice(), ordersDetail.getProduct().getPrice(),
            ordersDetail.getProduct().getPurchaseInquiry(),
            ordersDetail.getProduct().getOrigin(), ordersDetail.getProduct().getProducer(),
            ordersDetail.getProduct().getMainImage(), ordersDetail.getProduct().getImage1(),
            ordersDetail.getProduct().getImage2(), ordersDetail.getProduct().getImage3(),
            ordersDetail.getProduct().getViewCnt(), ordersDetail.getProduct().getStatus(),
            ordersDetail.getProduct().getCreateAt(), ordersDetail.getModifiedAt(),
            SellerVo.of(ordersDetail.getProduct().getSeller()),
            CategoryVo.of(ordersDetail.getProduct().getCategory()));
    }
}
