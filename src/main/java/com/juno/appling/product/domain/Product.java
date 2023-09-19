package com.juno.appling.product.domain;

import com.juno.appling.member.domain.Seller;
import com.juno.appling.product.dto.request.ProductRequest;
import com.juno.appling.product.dto.request.PutProductRequest;
import com.juno.appling.product.enums.Status;
import jakarta.persistence.*;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Status status;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

    private Product(Seller seller, Category category, String mainTitle, String mainExplanation,
        String productMainExplanation, String productSubExplanation, int originPrice, int price,
        String purchaseInquiry, String origin, String producer, String mainImage, String image1,
        String image2, String image3, Status status, LocalDateTime createAt,
        LocalDateTime modifiedAt) {
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
        this.viewCnt = 0L;
        this.status = status;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }

    public static Product of(Seller member, Category category, ProductRequest productRequest) {
        LocalDateTime now = LocalDateTime.now();

        Status status = Status.valueOf(Optional.ofNullable(productRequest.getStatus()).orElse("NORMAL").toUpperCase());
        return new Product(member, category, productRequest.getMainTitle(),
            productRequest.getMainExplanation(), productRequest.getProductMainExplanation(),
            productRequest.getProductSubExplanation(), productRequest.getOriginPrice(),
            productRequest.getPrice(), productRequest.getPurchaseInquiry(), productRequest.getOrigin(),
            productRequest.getProducer(), productRequest.getMainImage(), productRequest.getImage1(),
            productRequest.getImage2(), productRequest.getImage3(), status, now, now);
    }

    public void put(PutProductRequest putProductRequest) {
        LocalDateTime now = LocalDateTime.now();
        Status status = Status.valueOf(putProductRequest.getStatus().toUpperCase());
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
        this.status = status;
    }

    public void putCategory(Category category) {
        this.category = category;
    }

    public void addViewCnt() {
        this.viewCnt++;
    }
}
