package com.juno.appling.product.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.product.domain.Option;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public static List<OptionVo> getVoList(List<Option> optionList) {
        List<OptionVo> optionVoList = new LinkedList<>();
        for (Option option : optionList) {
            optionVoList.add(new OptionVo(option));
        }
        return optionVoList;
    }

    public static OptionVo of(Option option) {
        Optional<Option> optionalOption = Optional.ofNullable(option);
        return optionalOption.isPresent() ? new OptionVo(option) : null;
    }

    private OptionVo(Option option) {
        this.optionId = option.getId();
        this.name = option.getName();
        this.extraPrice = option.getExtraPrice();
        this.ea = option.getEa();
        this.createdAt = option.getCreatedAt();
        this.modifiedAt = option.getModifiedAt();
    }
}
