package com.juno.appling.member.infrastruceture;

import com.juno.appling.member.domain.MemberApplySeller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberApplySellerRepository extends JpaRepository<MemberApplySeller, Long> {

}
