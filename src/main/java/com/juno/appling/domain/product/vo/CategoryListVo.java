package com.juno.appling.domain.product.vo;

import com.juno.appling.config.base.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CategoryListVo extends BaseVo {
    private List<CategoryVo> list;
}
