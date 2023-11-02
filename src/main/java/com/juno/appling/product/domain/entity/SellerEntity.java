package com.juno.appling.product.domain.entity;

import com.juno.appling.member.controller.request.PutSellerRequest;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.product.domain.model.Seller;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seller")
public class SellerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @NotNull(message = "company 비어있을 수 없습니다.")
    @Column(unique = true)
    private String company;
    @NotNull(message = "tel 비어있을 수 없습니다.")
    @Column(length = 11)
    private String tel;
    @NotNull(message = "zonecode 비어있을 수 없습니다.")
    private String zonecode;
    @NotNull(message = "address 비어있을 수 없습니다.")
    private String address;
    @NotNull(message = "addressDetail 비어있을 수 없습니다.")
    private String addressDetail;
    @Email(message = "email 형식을 맞춰주세요.")
    @NotNull(message = "email 비어있을 수 없습니다.")
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static SellerEntity from(Seller seller) {
        SellerEntity sellerEntity = new SellerEntity();
        sellerEntity.id = seller.getId();
        sellerEntity.member = MemberEntity.from(seller.getMember());
        sellerEntity.company = seller.getCompany();
        sellerEntity.tel = seller.getTel();
        sellerEntity.zonecode = seller.getZonecode();
        sellerEntity.address = seller.getAddress();
        sellerEntity.addressDetail = seller.getAddressDetail();
        sellerEntity.email = seller.getEmail();
        sellerEntity.createdAt = seller.getCreatedAt();
        sellerEntity.modifiedAt = seller.getModifiedAt();
        return sellerEntity;
    }

    public Seller toModel() {
        return Seller.builder()
            .id(id)
            .member(member.toModel())
            .company(company)
            .tel(tel)
            .zonecode(zonecode)
            .address(address)
            .addressDetail(addressDetail)
            .email(email)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

    private SellerEntity(MemberEntity member, @NotNull(message = "company 비어있을 수 없습니다.") String company,
        @NotNull(message = "tel 비어있을 수 없습니다.") String tel,
        @NotNull(message = "zonecode 비어있을 수 없습니다.") String zonecode,
        @NotNull(message = "address 비어있을 수 없습니다.") String address,
        @NotNull(message = "addressDetail 비어있을 수 없습니다.") String addressDetail,
        @NotNull(message = "email 비어있을 수 없습니다.") String email, LocalDateTime createdAt,
        LocalDateTime modifiedAt) {

        this.member = member;
        this.company = company;
        this.tel = tel;
        this.zonecode = zonecode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.email = email;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public SellerEntity(Long sellerId, MemberEntity member, @NotNull(message = "company 비어있을 수 없습니다.") String company,
        @NotNull(message = "tel 비어있을 수 없습니다.") String tel,
        @NotNull(message = "zonecode 비어있을 수 없습니다.") String zonecode,
        @NotNull(message = "address 비어있을 수 없습니다.") String address,
        @NotNull(message = "addressDetail 비어있을 수 없습니다.") String addressDetail,
        @NotNull(message = "email 비어있을 수 없습니다.") String email) {

        LocalDateTime now = LocalDateTime.now();
        this.id = sellerId;
        this.member = member;
        this.company = company;
        this.tel = tel;
        this.zonecode = zonecode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.email = email;
        this.createdAt = now;
        this.modifiedAt = now;
    }

    public static SellerEntity of(MemberEntity memberEntity,
        @NotNull(message = "company 비어있을 수 없습니다.") String company,
        @NotNull(message = "tel 비어있을 수 없습니다.") String tel,
        @NotNull(message = "zonecode 비어있을 수 없습니다.") String zonecode,
        @NotNull(message = "address 비어있을 수 없습니다.") String address,
        @NotNull(message = "addressDetail 비어있을 수 없습니다.") String addressDetail,
        @NotNull(message = "email 비어있을 수 없습니다.") String email) {
        LocalDateTime now = LocalDateTime.now();
        tel = tel.replaceAll("-", "");
        return new SellerEntity(memberEntity, company, tel, zonecode, address, addressDetail, email, now, now);
    }

    public void put(PutSellerRequest dto) {
        LocalDateTime now = LocalDateTime.now();
        String tel = dto.getTel().replaceAll("-", "");
        this.modifiedAt = now;
        this.company = dto.getCompany();
        this.tel = tel;
        this.email = dto.getEmail();
        this.zonecode = dto.getZonecode();
        this.address = dto.getAddress();
        this.addressDetail = dto.getAddressDetail();
    }
}
