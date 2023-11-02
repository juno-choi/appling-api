package com.juno.appling.member.repository;

import com.juno.appling.member.domain.entity.RecipientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientJpaRepository extends JpaRepository<RecipientEntity, Long> {

}
