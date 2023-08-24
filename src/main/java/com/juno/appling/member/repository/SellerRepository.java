package com.juno.appling.member.repository;

import com.juno.appling.member.domain.entity.Member;
import com.juno.appling.member.domain.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByMember(Member member);
}
