package com.juno.appling.member.port;

import com.juno.appling.product.domain.entity.IntroduceEntity;
import com.juno.appling.product.domain.entity.SellerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroduceJpaRepository extends JpaRepository<IntroduceEntity, Long> {

    Optional<IntroduceEntity> findBySeller(SellerEntity SellerEntity);
}
