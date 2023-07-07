package com.juno.appling.domain.member.repository;

import com.juno.appling.domain.member.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
