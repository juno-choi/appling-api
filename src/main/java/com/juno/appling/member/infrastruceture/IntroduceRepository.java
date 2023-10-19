package com.juno.appling.member.infrastruceture;

import com.juno.appling.member.domain.Introduce;
import com.juno.appling.member.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntroduceRepository extends JpaRepository<Introduce, Long> {

    Optional<Introduce> findBySeller(Seller Seller);
}
