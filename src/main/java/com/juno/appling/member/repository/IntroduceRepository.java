package com.juno.appling.member.repository;

import com.juno.appling.member.domain.entity.Introduce;
import com.juno.appling.member.domain.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntroduceRepository extends JpaRepository<Introduce, Long> {

    Optional<Introduce> findBySeller(Seller Seller);
}
