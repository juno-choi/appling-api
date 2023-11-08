package com.juno.appling.product.domain.entity;

import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private SellerEntity seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OptionEntity> optionList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProductType type;

    public static ProductEntity from(Product product) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.id = product.getId();
        productEntity.seller = SellerEntity.from(product.getSeller());
        productEntity.category = CategoryEntity.from(product.getCategory());
        productEntity.mainTitle = product.getMainTitle();
        productEntity.mainExplanation = product.getMainExplanation();
        productEntity.productMainExplanation = product.getProductMainExplanation();
        productEntity.productSubExplanation = product.getProductSubExplanation();
        productEntity.originPrice = product.getOriginPrice();
        productEntity.price = product.getPrice();
        productEntity.purchaseInquiry = product.getPurchaseInquiry();
        productEntity.origin = product.getOrigin();
        productEntity.producer = product.getProducer();
        productEntity.mainImage = product.getMainImage();
        productEntity.image1 = product.getImage1();
        productEntity.image2 = product.getImage2();
        productEntity.image3 = product.getImage3();
        productEntity.viewCnt = product.getViewCnt();
        productEntity.status = product.getStatus();
        productEntity.ea = product.getEa();
        productEntity.createdAt = product.getCreatedAt();
        productEntity.modifiedAt = product.getModifiedAt();
        productEntity.type = product.getType();
        return productEntity;
    }

    public Product toModel() {
        if(type == ProductType.OPTION) {
            return Product.builder()
                    .id(id)
                    .seller(seller.toModel())
                    .category(category.toModel())
                    .mainTitle(mainTitle)
                    .mainExplanation(mainExplanation)
                    .productMainExplanation(productMainExplanation)
                    .productSubExplanation(productSubExplanation)
                    .originPrice(originPrice)
                    .price(price)
                    .purchaseInquiry(purchaseInquiry)
                    .origin(origin)
                    .producer(producer)
                    .mainImage(mainImage)
                    .image1(image1)
                    .image2(image2)
                    .image3(image3)
                    .viewCnt(viewCnt)
                    .status(status)
                    .ea(ea)
                    .createdAt(createdAt)
                    .modifiedAt(modifiedAt)
                    .type(type)
                    .optionList(optionList.stream().map(OptionEntity::toModel).collect(Collectors.toList()))
                    .build();
        }

        return Product.builder()
            .id(id)
            .seller(seller.toModel())
            .category(category.toModel())
            .mainTitle(mainTitle)
            .mainExplanation(mainExplanation)
            .productMainExplanation(productMainExplanation)
            .productSubExplanation(productSubExplanation)
            .originPrice(originPrice)
            .price(price)
            .purchaseInquiry(purchaseInquiry)
            .origin(origin)
            .producer(producer)
            .mainImage(mainImage)
            .image1(image1)
            .image2(image2)
            .image3(image3)
            .viewCnt(viewCnt)
            .status(status)
            .ea(ea)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .type(type)
            .build();
    }

    @Builder
    public ProductEntity(Long id, SellerEntity seller, CategoryEntity category, String mainTitle,
        String mainExplanation,
        String productMainExplanation, String productSubExplanation, int originPrice, int price,
        String purchaseInquiry, String origin, String producer, String mainImage, String image1,
        String image2, String image3, Long viewCnt, ProductStatus status, int ea,
        LocalDateTime createdAt, LocalDateTime modifiedAt, ProductType type) {
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
        this.type = type;
    }

    private ProductEntity(SellerEntity seller, CategoryEntity category, ProductRequest productRequest) {
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
        this.createdAt = now;
        this.modifiedAt = now;
    }

    public static ProductEntity of(SellerEntity member, CategoryEntity categoryEntity, ProductRequest productRequest) {
        return new ProductEntity(member, categoryEntity, productRequest);
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

    public void putCategory(CategoryEntity categoryEntity) {
        this.category = categoryEntity;
    }

    public void addViewCnt() {
        this.viewCnt++;
    }

    public void addOptionsList(OptionEntity optionEntity) {
        this.optionList.add(optionEntity);
    }


    public void addAllOptionsList(List<OptionEntity> optionEntities) {
        this.optionList.addAll(optionEntities);
    }

    public void minusEa(int ea, OptionEntity optionEntity) {
        if(this.type == ProductType.OPTION) {
            List<OptionEntity> optionEntityList = this.optionList;
            optionEntityList.stream().filter(o -> o.getId().equals(optionEntity.getId()))
                    .findFirst()
                    .ifPresent(o -> o.minusEa(ea)
            );
        } else {
            this.ea -= ea;
        }
    }
}
