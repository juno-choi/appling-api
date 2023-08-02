package com.juno.appling.member.repository;

import com.juno.appling.member.domain.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
}
