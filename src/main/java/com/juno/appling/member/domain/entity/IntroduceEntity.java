package com.juno.appling.member.domain.entity;

import com.juno.appling.member.enums.IntroduceStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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
