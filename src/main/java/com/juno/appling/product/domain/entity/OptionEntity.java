package com.juno.appling.product.domain.entity;

import com.juno.appling.product.controller.request.OptionRequest;
import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.enums.OptionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "options")
public class OptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    private String name;
    private int extraPrice;
    private int ea;
    @Enumerated(EnumType.STRING)
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    public static OptionEntity from(Option option) {
        OptionEntity optionEntity = new OptionEntity();
        optionEntity.id = option.getId();
        optionEntity.name = option.getName();
        optionEntity.extraPrice = option.getExtraPrice();
        optionEntity.ea = option.getEa();
        optionEntity.status = option.getStatus();
        optionEntity.createdAt = option.getCreatedAt();
        optionEntity.modifiedAt = option.getModifiedAt();
        return optionEntity;
    }

    public Option toModel(){
        return Option.builder()
            .id(id)
            .name(name)
            .extraPrice(extraPrice)
            .ea(ea)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }


    public OptionEntity(String name, int extraPrice, int ea, ProductEntity product) {
        LocalDateTime now = LocalDateTime.now();
        this.name = name;
        this.extraPrice = extraPrice;
        this.ea = ea;
        this.createdAt = now;
        this.modifiedAt = now;
        this.status = OptionStatus.NORMAL;
        this.product = product;
    }

    public static OptionEntity of(ProductEntity productEntity, OptionRequest optionRequest) {
        OptionEntity optionEntity = new OptionEntity(optionRequest.getName(), optionRequest.getExtraPrice(), optionRequest.getEa(),
            productEntity);
        return optionEntity;
    }

    public static List<OptionEntity> ofList(ProductEntity productEntity, List<OptionRequest> saveRequestOptionList) {
        List<OptionEntity> optionEntityList = new ArrayList<>();
        for (OptionRequest optionRequest : saveRequestOptionList) {
            OptionEntity optionEntity = OptionEntity.of(productEntity, optionRequest);
            optionEntityList.add(optionEntity);
        }
        return optionEntityList;
    }

    public void put(OptionRequest optionRequest) {
        this.name = optionRequest.getName();
        this.extraPrice = optionRequest.getExtraPrice();
        this.ea = optionRequest.getEa();
        this.modifiedAt = LocalDateTime.now();
        this.status = OptionStatus.valueOf(Optional.ofNullable(optionRequest.getStatus()).orElse("NORMAL").toUpperCase());
    }

    public void delete() {
        this.status = OptionStatus.DELETE;
    }

    public void minusEa(int ea) {
        this.ea -= ea;
    }
}
