package com.juno.appling.product.port;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.model.Member;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.product.domain.model.Seller;
import com.juno.appling.product.repository.SellerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerRepository{
    private final SellerJpaRepository sellerJpaRepository;


    @Override
    public Seller findById(Long id) {
        SellerEntity sellerEntity = sellerJpaRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 판매자입니다.")
        );
        return sellerEntity.toModel();
    }

    @Override
    public Seller findByMember(Member member) {
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(MemberEntity.from(member)).orElseThrow(
                () -> new IllegalArgumentException("유효하지 않은 판매자입니다.")
        );
        return sellerEntity.toModel();
    }
}
