package com.juno.appling.product.domain;

import com.juno.appling.product.controller.request.OptionRequest;
import com.juno.appling.product.enums.OptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "options")
public class Option {
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
    private Product product;

    public Option(String name, int extraPrice, int ea, Product product) {
        LocalDateTime now = LocalDateTime.now();
        this.name = name;
        this.extraPrice = extraPrice;
        this.ea = ea;
        this.createdAt = now;
        this.modifiedAt = now;
        this.status = OptionStatus.NORMAL;
        this.product = product;
    }

    public static Option of(Product product, OptionRequest optionRequest) {
        Option option = new Option(optionRequest.getName(), optionRequest.getExtraPrice(), optionRequest.getEa(), product);
        return option;
    }

    public static List<Option> ofList(Product product, List<OptionRequest> saveRequestOptionList) {
        List<Option> optionList = new ArrayList<>();
        for (OptionRequest optionRequest : saveRequestOptionList) {
            Option option = Option.of(product, optionRequest);
            optionList.add(option);
        }
        return optionList;
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
