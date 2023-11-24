package com.juno.appling.product.domain.repository;

import com.juno.appling.product.domain.entity.OptionEntity;
import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.port.OptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {
    private final OptionJpaRepository optionJpaRepository;

    @Override
    public Option save(Option option) {
        return optionJpaRepository.save(OptionEntity.from(option)).toModel();
    }

    @Override
    public Option findById(Long id) {
        OptionEntity optionEntity = optionJpaRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 옵션입니다.")
        );
        return optionEntity.toModel();
    }
}
