package com.juno.appling.domain.vo.product;

import com.juno.appling.domain.vo.BaseVo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CategoryVo extends BaseVo {
    @NotNull
    private Long categoryId;
    @NotNull
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
