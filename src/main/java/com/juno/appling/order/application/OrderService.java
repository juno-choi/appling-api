package com.juno.appling.order.application;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.order.domain.OrderRepository;
import com.juno.appling.order.domain.OrderItemRepository;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.TempOrderResponse;
import com.juno.appling.product.domain.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberUtil memberUtil;

    @Transactional
    public TempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request){
        // TODO 유저 확인 먼저
        Member member = memberUtil.getMember(request);


        return new TempOrderResponse(1L);
    }
}
