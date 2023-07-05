package com.juno.appling.domain.vo.product;

import com.juno.appling.domain.vo.BaseVo;
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
