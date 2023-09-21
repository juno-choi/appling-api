package com.juno.appling.order.application;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.order.domain.Orders;
import com.juno.appling.order.domain.OrdersDetail;
import com.juno.appling.order.domain.OrdersDetailRepository;
import com.juno.appling.order.domain.OrdersRepository;
import com.juno.appling.order.dto.request.TempOrderDto;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.ReceiptVo;
import com.juno.appling.order.dto.response.TempOrderResponse;
import com.juno.appling.order.dto.response.TempOrderVo;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.enums.Status;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final OrdersRepository ordersRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final MemberUtil memberUtil;

    @Transactional
    public TempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request){
        // TODO 유저 확인 먼저
        Member member = memberUtil.getMember(request);

        List<TempOrderDto> requestOrderList = tempOrderRequest.getOrderList();
        Map<Long, Integer> productEaMap = new HashMap<>();
        // 개수 확인을 위해 요청 정보를 map에 넣어둠
        for(TempOrderDto t : requestOrderList){
            productEaMap.put(t.getProductId(), t.getEa());
        }

        List<Long> orderProudctIdList = requestOrderList.stream()
            .map(o -> o.getProductId())
            .collect(Collectors.toList());
        List<Product> productList = productRepository.findAllById(orderProudctIdList);

        if(requestOrderList.size() != productList.size()){
            throw new IllegalArgumentException("유효하지 않은 상품 id가 존재합니다.");
        }

        /**
         * 주문 하기전 validation check
         * 체크할 점
         * 1. 주문 상품의 상태
         */
        List<Product> notNormalProductList = productList.stream()
            .filter(p -> !p.getStatus().equals(Status.NORMAL)).collect(Collectors.toList());

        if(!notNormalProductList.isEmpty()){
            for(Product p : notNormalProductList){
                log.error("[order][tempOrder][유효하지 않은 상품 주문 시도] member = {}, id = {}, name = {}", member.getId(), p.getId(), p.getMainTitle());
            }
            throw new IllegalArgumentException("유효하지 않은 상품이 존재합니다.");
        }

        // 주문 생성 로직
        List<ReceiptVo> receiptList = new ArrayList<>();
        List<TempOrderVo> orderList = new ArrayList<>();
        int totalPrice = 0;
        // 주문 생성
        Orders orders = Orders.of(member);
        Orders saveOrder = ordersRepository.save(orders);

        // 주문 detail 생성
        for(Product p : productList){
            int ea = productEaMap.get(p.getId());
            OrdersDetail ordersDetail = OrdersDetail.of(orders, p, ea);
            ordersDetailRepository.save(ordersDetail);

            // 총 금액 계산
            totalPrice += ordersDetail.getProductTotalPrice();

            // 영수증 계산
            receiptList.add(ReceiptVo.of(ordersDetail));

            // 주문 상품 리스트
            orderList.add(TempOrderVo.of(ordersDetail));
        }
        return new TempOrderResponse(saveOrder.getId(), totalPrice, receiptList, orderList);
    }
}
