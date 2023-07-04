package com.juno.appling.domain.entity.member;

import com.juno.appling.domain.enums.member.RecipientInfoStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class RecipientInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "precipient_info_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Member member;

    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String tel;

    @Enumerated(EnumType.STRING)
    private RecipientInfoStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
