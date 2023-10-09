package com.juno.appling.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
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
            product.getType(),
            product.getEa(),
            product.getCreateAt(),
            product.getModifiedAt(),
            new SellerResponse(product.getSeller().getId(), product.getSeller().getEmail(),
                product.getSeller().getCompany(), product.getSeller().getZonecode(),
                product.getSeller().getAddress(), product.getSeller().getAddressDetail(), product.getSeller().getTel()),
            new CategoryResponse(product.getCategory().getId(), product.getCategory().getName(),
                product.getCategory().getCreatedAt(), product.getCategory().getModifiedAt()),
            OptionVo.getVoList(product.getOptionList())
        );
    }
}
