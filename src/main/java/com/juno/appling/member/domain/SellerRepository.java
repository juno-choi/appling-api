package com.juno.appling.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByMember(Member member);

    Optional<Seller> findByCompany(String company);
}
