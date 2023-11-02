package com.juno.appling.member.domain.entity;

import com.juno.appling.member.controller.request.PostRecipientRequest;
import com.juno.appling.member.enums.RecipientInfoStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recipient")
public class RecipientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String addressDetail;
    @NotNull
    private String zonecode;
    @NotNull
    private String tel;

    @Enumerated(EnumType.STRING)
    private RecipientInfoStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private RecipientEntity(MemberEntity member, @NotNull String name, @NotNull String zonecode,
        @NotNull String address, @NotNull String addressDetail, @NotNull String tel, RecipientInfoStatus status,
        LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.member = member;
        this.name = name;
        this.zonecode = zonecode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.tel = tel;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static RecipientEntity of(MemberEntity memberEntity, @NotNull String name, @NotNull String zonecode,
        @NotNull String address, @NotNull String addressDetail, @NotNull String tel, RecipientInfoStatus status) {
        tel = tel.replaceAll("-", "");
        LocalDateTime now = LocalDateTime.now();
        return new RecipientEntity(memberEntity, name, zonecode, address, addressDetail, tel, status, now, now);
    }

    public void put(PostRecipientRequest postRecipientRequestInfo) {
        this.name = postRecipientRequestInfo.getName();
        this.zonecode = postRecipientRequestInfo.getZonecode();
        this.address = postRecipientRequestInfo.getAddress();
        this.tel = postRecipientRequestInfo.getTel();
        this.modifiedAt = LocalDateTime.now();
    }
}
