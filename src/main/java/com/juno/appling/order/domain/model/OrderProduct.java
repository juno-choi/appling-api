package com.juno.appling.order.domain.model;

import com.juno.appling.order.domain.entity.OrderOptionEntity;
import com.juno.appling.product.domain.model.Category;
import com.juno.appling.product.domain.model.Seller;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProduct {
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
    private List<OrderOption> optionList = new ArrayList<>();
    private ProductType type;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
