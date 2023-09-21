package com.juno.appling.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
public class ProductResponse {

    private Long id;
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
    private SellerResponse seller;
    private CategoryResponse category;

    public ProductResponse(Product product) {
        this(
            product.getId(),
            product.getMainTitle(),
            product.getMainExplanation(),
            product.getProductMainExplanation(),
            product.getProductSubExplanation(),
            product.getOriginPrice(),
            product.getPrice(),
            product.getPurchaseInquiry(),
            product.getOrigin(),
            product.getProducer(),
            product.getMainImage(),
            product.getImage1(),
            product.getImage2(),
            product.getImage3(),
            product.getViewCnt(),
            product.getStatus(),
            product.getCreateAt(),
            product.getModifiedAt(),
            new SellerResponse(product.getSeller().getId(), product.getSeller().getEmail(),
                product.getSeller().getCompany(), product.getSeller().getZonecode(),
                product.getSeller().getAddress(), product.getSeller().getAddressDetail(), product.getSeller().getTel()),
            new CategoryResponse(product.getCategory().getId(), product.getCategory().getName(),
                product.getCategory().getCreatedAt(), product.getCategory().getModifiedAt())
        );
    }
}
