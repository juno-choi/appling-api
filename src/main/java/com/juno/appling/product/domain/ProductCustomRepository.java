package com.juno.appling.product.domain;

import com.juno.appling.global.querydsl.QuerydslConfig;
import com.juno.appling.product.dto.response.ProductResponse;
import com.juno.appling.product.enums.ProductStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepository {

    private final QuerydslConfig q;


    public Page<ProductResponse> findAll(Pageable pageable, String search, ProductStatus productStatus,
                                         Category category, Long memberId) {
        QProduct product = QProduct.product;
        QOption option = QOption.option;
        BooleanBuilder builder = new BooleanBuilder();

        search = Optional.ofNullable(search).orElse("").trim();
        memberId = Optional.ofNullable(memberId).orElse(0L);
        Optional<Category> optionalCategory = Optional.ofNullable(category);

        if (!search.equals("")) {
            builder.and(product.mainTitle.contains(search));
        }
        if (memberId != 0L) {
            builder.and(product.seller.member.id.eq(memberId));
        }
        if (optionalCategory.isPresent()) {
            builder.and(product.category.eq(category));
        }
        builder.and(product.status.eq(productStatus));

        List<ProductResponse> content = q.query()
            .select(Projections.constructor(ProductResponse.class, product))
            .from(product)
            .where(builder)
            .orderBy(product.createAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        Long total = q.query().from(product).where(builder).stream().count();
        return new PageImpl<>(content, pageable, total);
    }

    public List<ProductResponse> findAllByIdList(List<Long> productIdList) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(product.id.in(productIdList));
        List<ProductResponse> content = q.query().select(Projections.constructor(ProductResponse.class,
                        product
                ))
                .from(product)
                .where(builder)
                .fetch();
        return content;
    }
}
