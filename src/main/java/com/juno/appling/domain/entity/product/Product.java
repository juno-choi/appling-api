package com.juno.appling.domain.entity.product;

import com.juno.appling.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Member member;

    private String mainTitle;
    private String mainExplanation;
    private String productMainExplanation;
    private String productSubExplanation;

    private Product(Member member, String mainTitle, String mainExplanation, String productMainExplanation, String productSubExplanation) {
        this.member = member;
        this.mainTitle = mainTitle;
        this.mainExplanation = mainExplanation;
        this.productMainExplanation = productMainExplanation;
        this.productSubExplanation = productSubExplanation;
    }

    public static Product of(Member member, String mainTitle, String mainExplanation, String productMainExplanation, String productSubExplanation) {
        return new Product(member, mainTitle, mainExplanation, productMainExplanation, productSubExplanation);
    }
}
