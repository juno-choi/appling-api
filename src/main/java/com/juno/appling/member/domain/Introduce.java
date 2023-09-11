package com.juno.appling.member.domain;

import com.juno.appling.member.enums.IntroduceStatus;
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

    private String subject;
    private String url;

    @Enumerated(EnumType.STRING)
    private IntroduceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private Introduce(Seller seller, String subject, String url, IntroduceStatus status,
        LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.seller = seller;
        this.subject = subject;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Introduce of(Seller seller, String subject, String url, IntroduceStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new Introduce(seller, subject, url, status, now, now);
    }

    public void changeUrl(String url){
        this.url = url;
    }

}
