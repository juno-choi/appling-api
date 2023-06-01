package com.juno.appling.domain.entity.member;

import com.juno.appling.domain.enums.member.MemberApplySellerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class MemberApplySeller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    @NotNull
    private Long memberId;

    @Enumerated(EnumType.STRING)
    private MemberApplySellerStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private MemberApplySeller(@NotNull Long memberId, MemberApplySellerStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.memberId = memberId;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static MemberApplySeller of(Long memberId){
        LocalDateTime now = LocalDateTime.now();
        return new MemberApplySeller(memberId, MemberApplySellerStatus.APPLY, now, now);
    }

    public void patchApplyStatus(MemberApplySellerStatus status){
        this.status = status;
    }
}
