package com.juno.appling.product.domain.model;

import com.juno.appling.product.enums.OptionStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Option {
    private Long id;
    private String name;
    private int extraPrice;
    private int ea;
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Product product;
}
