package com.juno.appling.domain.member.repository;

import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.member.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByMember(Member member);
}
