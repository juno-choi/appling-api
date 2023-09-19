package com.juno.appling.order.application;

import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.order.domain.Orders;
import com.juno.appling.order.domain.OrdersDetail;
import com.juno.appling.order.domain.OrdersDetailRepository;
import com.juno.appling.order.domain.OrdersRepository;
import com.juno.appling.order.dto.request.TempOrder;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.TempOrderResponse;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final MemberUtil memberUtil;

    @Transactional
    public TempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request){
        // TODO 유저 확인 먼저
        Member member = memberUtil.getMember(request);

        List<TempOrder> requestOrderList = tempOrderRequest.getOrderList();
        Map<Long, Integer> productEaMap = new HashMap<>();
        // 개수 확인을 위해 요청 정보를 map에 넣어둠
        for(TempOrder t : requestOrderList){
            productEaMap.put(t.getProductId(), t.getEa());
        }

        List<Long> orderProudctIdList = requestOrderList.stream()
            .map(o -> o.getProductId())
            .collect(Collectors.toList());
        List<Product> productList = productRepository.findAllById(orderProudctIdList);

        if(requestOrderList.size() != productList.size()){
            throw new IllegalArgumentException("유효하지 않은 상품 id가 존재합니다.");
        }

        // TODO 주문 생성

        Orders orders = Orders.of(member);
        Orders saveOrder = ordersRepository.save(orders);
        // TODO 주문 detail 생성
        for(Product p : productList){
            OrdersDetail ordersDetail = OrdersDetail.of(orders, p, productEaMap.get(p.getId()));
            ordersDetailRepository.save(ordersDetail);
        }
//        TempOrderResponse tempOrderResponse = new TempOrderResponse(saveOrder.getId(), );
        return null;
    }
}
