package com.juno.appling.member.repository;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.product.domain.entity.SellerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerJpaRepository extends JpaRepository<SellerEntity, Long> {

    Optional<SellerEntity> findByMember(MemberEntity memberEntity);

    Optional<SellerEntity> findByCompany(String company);
}
