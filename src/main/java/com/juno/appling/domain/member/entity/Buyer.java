package com.juno.appling.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_id")
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    @Column(length = 11)
    private String tel;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private Buyer(Long id, @NotNull String name, @NotNull String email, @NotNull String tel, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Buyer of(Long id, @NotNull String name, @NotNull String email, @NotNull String tel){
        LocalDateTime now = LocalDateTime.now();
        tel = tel.replaceAll("-", "");
        return new Buyer(id, name, email, tel, now, now);
    }

    public static Buyer ofEmpty(){
        return new Buyer(null, "", "", "", null, null);
    }

    public void put(@NotNull String name, @NotNull String email, @NotNull String tel){
        tel = tel.replaceAll("-", "");
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.modifiedAt = LocalDateTime.now();
    }
}
