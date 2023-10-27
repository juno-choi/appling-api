package com.juno.appling.product.domain;

import com.juno.appling.member.domain.Seller;
import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
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
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    private int ea;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Option> optionList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProductType type;

    private Product(Seller seller, Category category, ProductRequest productRequest) {
        LocalDateTime now = LocalDateTime.now();
        Integer ea = Optional.ofNullable(productRequest.getEa()).orElse(0);
        ProductStatus status = ProductStatus.valueOf(productRequest.getStatus().toUpperCase());
        ProductType type = ProductType.valueOf(productRequest.getType().toUpperCase());
        this.seller = seller;
        this.category = category;
        this.mainTitle = productRequest.getMainTitle();
        this.mainExplanation = productRequest.getMainExplanation();
        this.productMainExplanation = productRequest.getProductMainExplanation();
        this.productSubExplanation = productRequest.getProductSubExplanation();
        this.originPrice = productRequest.getOriginPrice();
        this.price = productRequest.getPrice();
        this.purchaseInquiry = productRequest.getPurchaseInquiry();
        this.origin = productRequest.getOrigin();
        this.producer = productRequest.getProducer();
        this.mainImage = productRequest.getMainImage();
        this.image1 = productRequest.getImage1();
        this.image2 = productRequest.getImage2();
        this.image3 = productRequest.getImage3();
        this.viewCnt = 0L;
        this.status = status;
        this.type = type;
        this.ea = ea;
        this.createAt = now;
        this.modifiedAt = now;
    }

    public static Product of(Seller member, Category category, ProductRequest productRequest) {
        return new Product(member, category, productRequest);
    }

    public void put(PutProductRequest putProductRequest) {
        LocalDateTime now = LocalDateTime.now();
        ProductStatus productStatus = ProductStatus.valueOf(putProductRequest.getStatus().toUpperCase());
        ProductType type = ProductType.valueOf(Optional.ofNullable(putProductRequest.getType()).orElse("NORMAL").toUpperCase());

        int ea = Optional.of(putProductRequest.getEa()).orElse(0);
        this.mainTitle = putProductRequest.getMainTitle();
        this.mainExplanation = putProductRequest.getMainExplanation();
        this.productMainExplanation = putProductRequest.getProductMainExplanation();
        this.productSubExplanation = putProductRequest.getProductSubExplanation();
        this.originPrice = putProductRequest.getOriginPrice();
        this.price = putProductRequest.getPrice();
        this.purchaseInquiry = putProductRequest.getPurchaseInquiry();
        this.origin = putProductRequest.getOrigin();
        this.producer = putProductRequest.getProducer();
        this.mainImage = putProductRequest.getMainImage();
        this.image1 = putProductRequest.getImage1();
        this.image2 = putProductRequest.getImage2();
        this.image3 = putProductRequest.getImage3();
        this.modifiedAt = now;
        this.status = productStatus;
        this.type = type;
        this.ea = ea;
    }

    public void putCategory(Category category) {
        this.category = category;
    }

    public void addViewCnt() {
        this.viewCnt++;
    }

    public void addOptionsList(Option option) {
        this.optionList.add(option);
    }


    public void addAllOptionsList(List<Option> options) {
        this.optionList.addAll(options);
    }

    public void minusEa(int ea) {
        this.ea -= ea;
    }
}
