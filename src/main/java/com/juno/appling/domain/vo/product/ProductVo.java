package com.juno.appling.domain.vo.product;

import com.juno.appling.domain.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductVo extends BaseVo {
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
}
