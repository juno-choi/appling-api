package com.juno.appling.repository.member;

import com.juno.appling.domain.entity.member.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
}
