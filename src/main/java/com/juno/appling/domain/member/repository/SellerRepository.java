package com.juno.appling.domain.member.repository;

import com.juno.appling.domain.member.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
