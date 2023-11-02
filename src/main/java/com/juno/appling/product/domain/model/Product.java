package com.juno.appling.product.domain.model;

import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Product {
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<Option> optionList = new ArrayList<>();
    private ProductType type;

    @Builder
    public Product(Long id, Seller seller, Category category, String mainTitle,
        String mainExplanation,
        String productMainExplanation, String productSubExplanation, int originPrice, int price,
        String purchaseInquiry, String origin, String producer, String mainImage, String image1,
        String image2, String image3, Long viewCnt, ProductStatus status, int ea,
        LocalDateTime createdAt, LocalDateTime modifiedAt, List<Option> optionList,
        ProductType type) {
        this.id = id;
        this.seller = seller;
        this.category = category;
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
        this.ea = ea;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.optionList = optionList;
        this.type = type;
    }
}
