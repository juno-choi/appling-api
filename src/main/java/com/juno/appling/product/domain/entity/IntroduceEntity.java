package com.juno.appling.product.domain.entity;

import com.juno.appling.member.enums.IntroduceStatus;
import com.juno.appling.product.domain.model.Introduce;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "introduce")
public class IntroduceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduce_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private SellerEntity seller;

    private String subject;
    private String url;

    @Enumerated(EnumType.STRING)
    private IntroduceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static IntroduceEntity from(Introduce introduce) {
        IntroduceEntity introduceEntity = new IntroduceEntity();
        introduceEntity.id = introduce.getId();
        introduceEntity.seller = SellerEntity.from(introduce.getSeller());
        introduceEntity.subject = introduce.getSubject();
        introduceEntity.url = introduce.getUrl();
        introduceEntity.status = introduce.getStatus();
        introduceEntity.createdAt = introduce.getCreatedAt();
        introduceEntity.modifiedAt = introduce.getModifiedAt();
        return introduceEntity;
    }

    private IntroduceEntity(SellerEntity seller, String subject, String url, IntroduceStatus status,
        LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.seller = seller;
        this.subject = subject;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static IntroduceEntity of(SellerEntity sellerEntity, String subject, String url, IntroduceStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new IntroduceEntity(sellerEntity, subject, url, status, now, now);
    }

    public void changeUrl(String url){
        this.url = url;
    }

}
