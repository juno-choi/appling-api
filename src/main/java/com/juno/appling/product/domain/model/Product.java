package com.juno.appling.product.domain.model;

import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<Option> optionList = new ArrayList<>();
    private ProductType type;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

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
        this.type = type;
        this.optionList = optionList;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void checkInStock(TempOrderDto tempOrderDto) {
        int ea = tempOrderDto.getEa();
        if (this.type == ProductType.NORMAL) {
            if (this.ea < ea) {
                throw new IllegalArgumentException(String.format("재고가 부족합니다! 현재 재고 = %s개", this.ea));
            }
        } else if (this.type == ProductType.OPTION) {
            Long optionId = tempOrderDto.getOptionId();
            Option option = this.optionList.stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("유효하지 않은 옵션입니다. option id = %s", optionId)));
            option.checkInStock(tempOrderDto);
        }
    }

    public void checkInStock(int ea, Long optionId) {
        if (this.type == ProductType.NORMAL) {
            if (this.ea < ea) {
                throw new IllegalArgumentException(String.format("재고가 부족합니다! 현재 재고 = %s개", this.ea));
            }
        } else if (this.type == ProductType.OPTION) {
            Option option = this.optionList.stream()
                    .filter(o -> o.getId().equals(optionId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("유효하지 않은 옵션입니다. option id = %s", optionId)));
            option.checkInStock(ea);
        }
    }

    public void minusEa(int ea) {
        if (this.type == ProductType.NORMAL) {
            this.ea -= ea;
        }
    }
}
