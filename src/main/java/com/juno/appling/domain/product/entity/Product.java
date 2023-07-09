package com.juno.appling.domain.product.entity;

import com.juno.appling.domain.product.dto.PutProductDto;
import com.juno.appling.domain.product.dto.ProductDto;
import com.juno.appling.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

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
    private int viewCnt;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

    private Product(Member member, Category category, String mainTitle, String mainExplanation, String productMainExplanation, String productSubExplanation, int originPrice, int price, String purchaseInquiry, String origin, String producer, String mainImage, String image1, String image2, String image3, LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.member = member;
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
        this.viewCnt = 0;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }

    public static Product of(Member member, Category category, ProductDto productDto){
        LocalDateTime now = LocalDateTime.now();
        return new Product(member, category, productDto.getMainTitle(), productDto.getMainExplanation(), productDto.getProductMainExplanation(), productDto.getProductSubExplanation(), productDto.getOriginPrice(), productDto.getPrice(), productDto.getPurchaseInquiry(), productDto.getOrigin(), productDto.getProducer(), productDto.getMainImage(), productDto.getImage1(), productDto.getImage2(), productDto.getImage3(), now, now);
    }

    public void put(PutProductDto putProductDto){
        LocalDateTime now = LocalDateTime.now();

        this.mainTitle = putProductDto.getMainTitle();
        this.mainExplanation = putProductDto.getMainExplanation();
        this.productMainExplanation = putProductDto.getProductMainExplanation();
        this.productSubExplanation = putProductDto.getProductSubExplanation();
        this.originPrice = putProductDto.getOriginPrice();
        this.price = putProductDto.getPrice();
        this.purchaseInquiry = putProductDto.getPurchaseInquiry();
        this.origin = putProductDto.getOrigin();
        this.producer = putProductDto.getProducer();
        this.mainImage = putProductDto.getMainImage();
        this.image1 = putProductDto.getImage1();
        this.image2 = putProductDto.getImage2();
        this.image3 = putProductDto.getImage3();
        this.modifiedAt = now;
    }

    public void putCategory(Category category){
        this.category = category;
    }

    public void addViewCnt(){
        this.viewCnt++;
    }
}
