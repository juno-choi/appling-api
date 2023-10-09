package com.juno.appling.product.domain;

import com.juno.appling.product.dto.request.OptionRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    public Option(String name, int extraPrice, int ea, Product product) {
        LocalDateTime now = LocalDateTime.now();
        this.name = name;
        this.extraPrice = extraPrice;
        this.ea = ea;
        this.createdAt = now;
        this.modifiedAt = now;
        this.product = product;
    }

    public static Option of(Product product, OptionRequest optionRequest) {
        Option option = new Option(optionRequest.getName(), optionRequest.getExtraPrice(), optionRequest.getEa(), product);
        return option;
    }
}
