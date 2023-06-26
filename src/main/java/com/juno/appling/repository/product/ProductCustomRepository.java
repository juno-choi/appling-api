package com.juno.appling.repository.product;

import com.juno.appling.common.querydsl.QuerydslConfig;
import com.juno.appling.domain.entity.product.QProduct;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.domain.vo.product.SellerVo;
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

    public Page<ProductVo> findAll(Pageable pageable, String search){
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        search = Optional.ofNullable(search).orElse("").trim();

        if(!search.equals("")){
            builder.and(product.mainTitle.contains(search));
        }

        List<ProductVo> content = q.query().select(Projections.constructor(ProductVo.class,
                    product.id,
                    product.mainTitle,
                    product.mainExplanation,
                    product.productMainExplanation,
                    product.productSubExplanation,
                    product.originPrice,
                    product.price,
                    product.purchaseInquiry,
                    product.origin,
                    product.producer,
                    product.mainImage,
                    product.image1,
                    product.image2,
                    product.image3,
                    product.createAt,
                    product.modifiedAt,
                    Projections.constructor(SellerVo.class,
                        product.member.id,
                        product.member.email,
                        product.member.nickname,
                        product.member.name
                    )
                ))
                .from(product)
                .where(builder)
                .orderBy(product.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = q.query().from(product).where(builder).stream().count();
        return new PageImpl<>(content, pageable, total);
    }
}
