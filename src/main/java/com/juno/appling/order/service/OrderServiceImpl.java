package com.juno.appling.order.service;

import com.github.dockerjava.api.exception.UnauthorizedException;
import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.model.Member;
import com.juno.appling.member.repository.SellerJpaRepository;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.OrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.controller.response.OrderInfoResponse;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.domain.model.OrderOption;
import com.juno.appling.order.domain.model.OrderProduct;
import com.juno.appling.order.controller.vo.OrderVo;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.order.port.OrderItemRepository;
import com.juno.appling.order.port.OrderOptionRepository;
import com.juno.appling.order.port.OrderProductRepository;
import com.juno.appling.order.port.OrderRepository;
import com.juno.appling.order.repository.OrderCustomJpaRepositoryImpl;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.port.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{
    private final MemberUtil memberUtil;
    private final SellerJpaRepository sellerJpaRepository;
    private final OrderCustomJpaRepositoryImpl orderCustomRepositoryImpl;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    @Override
    public PostTempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request){

        /**
         * 1. 주문 발급
         * 2. 주문 상품 등록
         * 3. 판매자 리스트 등록
         * 4. 상품 타입 체크
         * 4. 재고 확인
         */

        // 주문 발급
        Member member = memberUtil.getMember(request).toModel();
        List<Long> productIdList = tempOrderRequest.getOrderList().stream().mapToLong(TempOrderDto::getProductId).boxed().toList();
        List<Product> productList = productRepository.findAllById(productIdList);
        // 주문 상품 등록
        Order createOrder = Order.create(member, productList);
        Order order = orderRepository.save(createOrder);

        // orderProduct orderOption 등록
        Map<Long, Product> productMap = new HashMap<>();
        for (Product product : productList) {
            productMap.put(product.getId(), product);
        }

        List<OrderItem> orderItemList = new LinkedList<>();
        for (TempOrderDto tempOrderDto : tempOrderRequest.getOrderList()) {
            Product product = productMap.get(tempOrderDto.getProductId());

            //재고 확인
            product.checkInStock(tempOrderDto);

            //OrderProduct 생성
            OrderProduct createOrderProduct = OrderProduct.create(product);
            OrderProduct orderProduct = orderProductRepository.save(createOrderProduct);
            //OrderOption 생성
            OrderOption orderOption = null;
            if(product.getType() == ProductType.OPTION){
                Long optionId = tempOrderDto.getOptionId();
                OrderOption createOrderOption = OrderOption.create(product.getOptionList(), optionId);
                orderOption = orderOptionRepository.save(createOrderOption);
            }

            //OrderItem 생성
            OrderItem orderItem = OrderItem.create(order, orderProduct, orderOption, tempOrderDto.getEa());
            orderItemList.add(orderItem);
        }

        orderItemRepository.saveAll(orderItemList);
        order.createOrderNumber();
        orderRepository.save(order);
        return PostTempOrderResponse.builder().orderId(order.getId()).build();
    }

    @Override
    public OrderInfoResponse getOrderInfo(Long orderId, HttpServletRequest request){
        /**
         * order id와 member 정보로 임시 정보를 불러옴
         *
         * member 정보, order 정보 불러오기
         * 유효한 주문인지 체크
         * 해당 정보가 유저 정보가 맞는지 체크
         */

        Member member = memberUtil.getMember(request).toModel();

        Order order = orderRepository.findById(orderId);
        order.checkOrder(member);

        return OrderInfoResponse.create(order);
    }

    /**
     * 주문 정보를 update 해야됨!
     * 1. 주문 상태 변경
     * 2. 주문자, 수령자 정보 등록
     * 3. 주문 번호 만들기
     *
     * @param  completeOrderRequest   주문 완료 요청 객체
     * @param  request                HTTP 요청 객체
     * @return                        완료된 주문 응답 객체
     */
    @Transactional
    @Override
    public CompleteOrderResponse completeOrder(CompleteOrderRequest completeOrderRequest, HttpServletRequest request){
        /**
         * 주문 정보를 update 해야됨!
         * 1. 주문 상태 변경
         * 2. 주문자, 수령자 정보 등록
         * 3. 주문 번호 만들기
         */

        return CompleteOrderResponse.from(null);
    }


    @Override
    public OrderResponse getOrderListBySeller(Pageable pageable, String search, String status, HttpServletRequest request) {
        MemberEntity memberEntity = memberUtil.getMember(request);
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity)
                .orElseThrow(() -> new UnauthorizedException("잘못된 접근입니다."));

        /**
         * 1. order item 중 seller product가 있는 리스트 불러오기
         * 2. response data 만들기
         */
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));

        Page<OrderVo> orderList = orderCustomRepositoryImpl.findAllBySeller(pageable, search, orderStatus,
            sellerEntity);

        return OrderResponse.from(orderList);
    }
}