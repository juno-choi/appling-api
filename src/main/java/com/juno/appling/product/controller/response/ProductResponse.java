package com.juno.appling.product.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.vo.OptionVo;
import com.juno.appling.product.enums.OptionStatus;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class ProductResponse {

    private Long productId;
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
    private ProductType type;
    private int ea;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private SellerResponse seller;
    private CategoryResponse category;
    private List<OptionVo> optionList;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
            .productId(product.getId())
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
            .type(product.getType())
            .ea(product.getEa())
            .createdAt(product.getCreateAt())
            .modifiedAt(product.getModifiedAt())
            .seller(SellerResponse.from(product.getSeller()))
            .category(CategoryResponse.from(product))
            .optionList(OptionVo.getVoList(product.getOptionList().stream().filter(o -> o.getStatus().equals(OptionStatus.NORMAL)).toList()))
            .build();
    }
}
