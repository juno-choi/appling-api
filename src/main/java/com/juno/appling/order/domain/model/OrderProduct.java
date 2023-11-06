package com.juno.appling.order.domain.model;

import com.juno.appling.order.domain.entity.OrderOptionEntity;
import com.juno.appling.product.domain.model.Category;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.domain.model.Seller;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProduct {
    private Long id;
    private Seller seller;
    private Category category;
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
    private int ea;
    private List<OrderOption> optionList = new ArrayList<>();
    private ProductType type;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static OrderProduct create(Product product) {
        return OrderProduct.builder()
            .seller(product.getSeller())
            .category(product.getCategory())
            .mainTitle(product.getMainTitle())
            .mainExplanation(product.getMainExplanation())
            .productMainExplanation(product.getProductMainExplanation())
            .productSubExplanation(product.getProductSubExplanation())
            .originPrice(product.getOriginPrice())
            .price(product.getPrice())
            .purchaseInquiry(product.getPurchaseInquiry())
            .origin(product.getOrigin())
            .producer(product.getProducer())
            .mainImage(product.getMainImage())
            .image1(product.getImage1())
            .image2(product.getImage2())
            .image3(product.getImage3())
            .viewCnt(product.getViewCnt())
            .status(product.getStatus())
            .ea(product.getEa())
            .optionList(
                Optional.ofNullable(product.getOptionList()).orElse(new ArrayList<>()).stream().map(OrderOption::create).toList())
            .type(product.getType())
            .createdAt(product.getCreatedAt())
            .modifiedAt(product.getModifiedAt())
            .build();
    }
}
