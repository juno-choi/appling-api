package com.juno.appling.order.application;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.order.domain.*;
import com.juno.appling.order.dto.request.TempOrderDto;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.TempOrderResponse;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.enums.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Member member = memberUtil.getMember(request);

        /**
         * 1. 주문 발급
         * 2. 주문 상품 등록
         * 3. 판매자 리스트 등록
         */

        // 주문 발급
        StringBuffer sb = new StringBuffer();
        List<TempOrderDto> requestOrderProductList = tempOrderRequest.getOrderList();
        List<Long> requestProductIdList = requestOrderProductList.stream().mapToLong(o -> o.getProductId())
                .boxed().collect(Collectors.toList());

        List<Product> productList = productRepository.findAllById(requestProductIdList);
        productList = productList.stream().sorted((p1, p2) -> p2.getPrice() - p1.getPrice()).collect(Collectors.toList());

        if((requestOrderProductList.size() != productList.size()) || productList.size() == 0){
            throw new IllegalArgumentException("유효하지 않은 상품이 존재합니다.");
        }

        sb.append(productList.get(0).getMainTitle());
        if(productList.size() > 1){
            sb.append(" 외 ");
            sb.append(productList.size() -1);
            sb.append("개");
        }
        Order saveOrder = orderRepository.save(Order.of(member, sb.toString()));

        // 주문 상품 등록
        Map<Long, Integer> eaMap = new HashMap<>();
        for(TempOrderDto o : requestOrderProductList){
            eaMap.put(o.getProductId(), o.getEa());
        }

        for(Product p : productList){
            if(p.getStatus() != Status.NORMAL){
                throw new IllegalArgumentException("상품 상태가 유효하지 않습니다.");
            }

            int ea = eaMap.get(p.getId());
            OrderItem orderItem = orderItemRepository.save(OrderItem.of(saveOrder, p, ea));
            saveOrder.getOrderItemList().add(orderItem);
        }

        return new TempOrderResponse(saveOrder.getId());
    }
}
