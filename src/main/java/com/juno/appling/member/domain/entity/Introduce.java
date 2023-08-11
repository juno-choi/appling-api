package com.juno.appling.member.domain.entity;

import com.juno.appling.member.domain.enums.IntroduceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Introduce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduce_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private String url;

    @Enumerated(EnumType.STRING)
    private IntroduceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private Introduce(Seller seller, String url, IntroduceStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.seller = seller;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Introduce of(Seller seller, String url, IntroduceStatus status){
        LocalDateTime now = LocalDateTime.now();
        return new Introduce(seller, url, status, now, now);
    }

}
