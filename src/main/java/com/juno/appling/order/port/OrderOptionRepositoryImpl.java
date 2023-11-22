package com.juno.appling.order.port;

import com.juno.appling.order.domain.entity.OrderOptionEntity;
import com.juno.appling.order.domain.model.OrderOption;
import com.juno.appling.order.repository.OrderOptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderOptionRepositoryImpl implements OrderOptionRepository {
    private final OrderOptionJpaRepository orderOptionJpaRepository;

    @Override
    public void saveAll(Iterable<OrderOption> orderOptionList) {
        List<OrderOptionEntity> orderOptionEntityList = new ArrayList<>();
        for (OrderOption o : orderOptionList) {
            orderOptionEntityList.add(OrderOptionEntity.from(o));
        }
        orderOptionJpaRepository.saveAll(orderOptionEntityList).forEach(OrderOptionEntity::toModel);
    }

    @Override
    public OrderOption save(OrderOption orderOption) {
        return orderOptionJpaRepository.save(OrderOptionEntity.from(orderOption)).toModel();
    }
}
