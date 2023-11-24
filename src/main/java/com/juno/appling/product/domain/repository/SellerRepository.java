package com.juno.appling.product.domain.repository;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.product.domain.model.Seller;

public interface SellerRepository {
    Seller findById(Long id);
    Seller findByMember(Member member);
}
