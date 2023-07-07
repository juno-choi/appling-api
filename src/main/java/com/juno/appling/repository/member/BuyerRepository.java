package com.juno.appling.repository.member;

import com.juno.appling.domain.entity.member.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
