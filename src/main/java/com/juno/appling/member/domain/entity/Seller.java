package com.juno.appling.member.domain.entity;

import com.juno.appling.member.domain.dto.PutSellerDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

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
    @Email(message = "email 형식을 맞춰주세요.")
    @NotNull(message = "email 비어있을 수 없습니다.")
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private Seller(Member member, @NotNull(message = "company 비어있을 수 없습니다.") String company, @NotNull(message = "tel 비어있을 수 없습니다.") String tel, @NotNull(message = "zonecode 비어있을 수 없습니다.") String zonecode, @NotNull(message = "address 비어있을 수 없습니다.") String address, @NotNull(message = "email 비어있을 수 없습니다.") String email, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.member = member;
        this.company = company;
        this.tel = tel;
        this.zonecode = zonecode;
        this.address = address;
        this.email = email;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Seller of(Member member, @NotNull(message = "company 비어있을 수 없습니다.") String company, @NotNull(message = "tel 비어있을 수 없습니다.") String tel, @NotNull(message = "zonecode 비어있을 수 없습니다.") String zonecode, @NotNull(message = "address 비어있을 수 없습니다.") String address, @NotNull(message = "email 비어있을 수 없습니다.") String email){
        LocalDateTime now = LocalDateTime.now();
        tel = tel.replaceAll("-", "");
        return new Seller(member, company, tel, zonecode, address, email, now, now);
    }

    public void put(PutSellerDto dto){
        LocalDateTime now = LocalDateTime.now();
        String tel = dto.getTel().replaceAll("-","");
        this.modifiedAt = now;
        this.company = dto.getCompany();
        this.tel = tel;
        this.email = dto.getEmail();
        this.zonecode = dto.getZonecode();
        this.address = dto.getAddress();
    }
}
