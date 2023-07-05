package com.juno.appling.domain.entity.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class BuyerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_info_id")
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
}
