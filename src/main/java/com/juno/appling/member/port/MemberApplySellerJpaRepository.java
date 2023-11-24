package com.juno.appling.member.port;

import com.juno.appling.member.domain.entity.MemberApplySellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberApplySellerJpaRepository extends JpaRepository<MemberApplySellerEntity, Long> {

}
