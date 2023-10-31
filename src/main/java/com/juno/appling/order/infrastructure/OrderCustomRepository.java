package com.juno.appling.order.infrastructure;

import com.juno.appling.member.domain.Seller;
import com.juno.appling.order.domain.vo.OrderVo;
import com.juno.appling.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {

    Page<OrderVo> findAllBySeller(Pageable pageable, String search, OrderStatus status, Seller seller);
}
