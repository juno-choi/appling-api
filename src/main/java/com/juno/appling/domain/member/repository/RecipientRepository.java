package com.juno.appling.domain.member.repository;

import com.juno.appling.domain.member.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
}
