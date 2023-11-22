package com.juno.appling.order.port;

import com.juno.appling.order.domain.model.OrderOption;

public interface OrderOptionRepository {
    void saveAll(Iterable<OrderOption> orderOptionList);

    OrderOption save(OrderOption orderOption);
}
