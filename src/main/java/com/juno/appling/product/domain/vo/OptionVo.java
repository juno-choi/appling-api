package com.juno.appling.product.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.entity.OrderOptionEntity;
import com.juno.appling.product.domain.entity.OptionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
public class OptionVo {
    private Long optionId;
    private String name;
    private int extraPrice;
    private int ea;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static List<OptionVo> getVoList(List<OptionEntity> optionEntityList) {
        List<OptionVo> optionVoList = new LinkedList<>();
        for (OptionEntity optionEntity : optionEntityList) {
            optionVoList.add(new OptionVo(optionEntity));
        }
        return optionVoList;
    }

    public static OptionVo of(OrderOptionEntity option) {
        Optional<OrderOptionEntity> optionalOption = Optional.ofNullable(option);
        return optionalOption.isPresent() ? new OptionVo(option) : null;
    }

    private OptionVo(OptionEntity optionEntity) {
        this.optionId = optionEntity.getId();
        this.name = optionEntity.getName();
        this.extraPrice = optionEntity.getExtraPrice();
        this.ea = optionEntity.getEa();
        this.createdAt = optionEntity.getCreatedAt();
        this.modifiedAt = optionEntity.getModifiedAt();
    }
    private OptionVo(OrderOptionEntity orderOption) {
        this.optionId = orderOption.getOptionId();
        this.name = orderOption.getName();
        this.extraPrice = orderOption.getExtraPrice();
        this.createdAt = orderOption.getCreatedAt();
        this.modifiedAt = orderOption.getModifiedAt();
    }
}
