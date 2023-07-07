package com.juno.appling.domain.product.vo;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.config.base.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductListVo extends BaseVo {
    private int totalPage;
    private Long totalElements;
    private int numberOfElements;
    private Boolean last;
    private Boolean empty;
    private List<ProductVo> list;
}
