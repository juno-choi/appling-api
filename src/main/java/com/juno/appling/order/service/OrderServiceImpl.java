package com.juno.appling.order.service;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.controller.request.*;
import com.juno.appling.order.controller.response.*;
import com.juno.appling.order.domain.model.*;
import com.juno.appling.order.domain.repository.*;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.domain.model.Seller;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.domain.repository.OptionRepository;
import com.juno.appling.product.domain.repository.ProductRepository;
import com.juno.appling.product.domain.repository.SellerRepository;
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
public class OrderServiceImpl implements OrderService {
    private final MemberUtil memberUtil;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final SellerRepository sellerRepository;

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

            //OrderOption 생성
            OrderOption orderOption = null;
            if(product.getType() == ProductType.OPTION){
                Long optionId = tempOrderDto.getOptionId();
                OrderOption createOrderOption = OrderOption.create(product.getOptionList(), optionId);
                orderOption = orderOptionRepository.save(createOrderOption);
                createOrderProduct.addOption(orderOption);
            }
            OrderProduct orderProduct = orderProductRepository.save(createOrderProduct);
            //OrderItem 생성
            OrderItem orderItem = OrderItem.create(order, orderProduct, tempOrderDto.getEa());
            orderItemList.add(orderItem);
        }

        orderItemRepository.saveAll(orderItemList);
        orderRepository.save(order);
        return PostTempOrderResponse.builder().orderId(order.getId()).build();
    }

    @Override
    public TempOrderResponse getOrderInfo(Long orderId, HttpServletRequest request){
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

        return TempOrderResponse.create(order);
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
         *
         * 재고 확인
         * 상품 재고 마이너스 처리
         * 배송 정보 등록
         * 주문과 주문 상품 상태 수정
         * 주문 번호 생성
         */

        // 주문 확인
        Long orderId = completeOrderRequest.getOrderId();
        Order order = orderRepository.findById(orderId);
        order.checkOrder(memberUtil.getMember(request).toModel());

        // 재고 확인
        order.getOrderItemList().forEach(orderItem -> {
            OrderProduct orderProduct = orderItem.getOrderProduct();
            Long productId = orderProduct.getProductId();
            Product product = productRepository.findById(productId);
            ProductType type = product.getType();

            int ea = orderItem.getEa();
            Long optionId = orderItem.getOrderProduct().getOrderOption() == null ? null : orderItem.getOrderProduct().getOrderOption().getOptionId();

            // 상품 마이너스 처리
            if (type == ProductType.NORMAL) {
                product.checkInStock(ea, optionId);
                product.minusEa(ea);
                productRepository.save(product);
            } else if (type == ProductType.OPTION) {
                Option option = optionRepository.findById(optionId);
                option.checkInStock(ea);
                option.minusEa(ea);
                optionRepository.save(option);
            }

            // 배송 정보 등록
        });
        Delivery delivery = deliveryRepository.save(Delivery.create(completeOrderRequest));

        order.ordered();
        order.createOrderNumber();
        order.delivery(delivery);
        orderRepository.save(order);

        return CompleteOrderResponse.from(order);
    }


    @Override
    public OrderListResponse getOrderListBySeller(Pageable pageable, String search, String status, HttpServletRequest request) {

        /**
         * member로 seller 정보 가져오기
         * 주문 정보를 seller id로 가져오기
         */
        Member member = memberUtil.getMember(request).toModel();
        Seller seller = sellerRepository.findByMember(member);
        OrderStatus orderStatus = status == null ? null : OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Page<Order> orderPage = orderRepository.findAllBySeller(pageable, search, orderStatus, seller);

        return OrderListResponse.from(orderPage);
    }

    @Override
    public OrderResponse getOrderDetailBySeller(Long orderId, HttpServletRequest request) {
        Member member = memberUtil.getMember(request).toModel();
        Seller seller = sellerRepository.findByMember(member);
        Order order = orderRepository.findByIdAndSeller(orderId, seller);
        return OrderResponse.from(order);
    }

    @Override
    public OrderListResponse getOrderListByMember(Pageable pageable, String search, String status, HttpServletRequest request) {
        Member member = memberUtil.getMember(request).toModel();
        OrderStatus orderStatus = status == null ? null : OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Page<Order> orderPage = orderRepository.findAllByMember(pageable, search, orderStatus, member);
        return OrderListResponse.from(orderPage);
    }

    @Override
    public OrderResponse getOrderDetailByMember(Long orderId, HttpServletRequest request) {
        Member member = memberUtil.getMember(request).toModel();
        Order order = orderRepository.findById(orderId);
        order.checkOrder(member);
        return OrderResponse.from(order);
    }

    @Override
    @Transactional
    public void cancelOrder(CancelOrderRequest cancelOrderRequest, HttpServletRequest request) {
        Member member = memberUtil.getMember(request).toModel();
        Order order = orderRepository.findById(cancelOrderRequest.getOrderId());
        order.checkOrder(member);
        orderCancel(order);
    }

    private void orderCancel(Order order) {
        order.cancel();
        orderRepository.save(order);

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(order);
        orderItemList.forEach(orderItem -> {
            orderItem.cancel();
            orderItem.order(order);
            orderItemRepository.save(orderItem);

            OrderProduct orderProduct = orderItem.getOrderProduct();
            ProductType type = orderProduct.getType();
            Long optionId = Optional.ofNullable(orderProduct.getOrderOption())
                    .orElse(OrderOption.builder().optionId(0L).build()).getOptionId();
            int ea = orderItem.getEa();
            Product product = productRepository.findById(orderProduct.getProductId());

            // 상품 플러스 처리
            if (type == ProductType.NORMAL) {
                product.plusEa(ea);
                productRepository.save(product);
            } else if (type == ProductType.OPTION) {
                Option option = optionRepository.findById(optionId);
                option.plusEa(ea);
                optionRepository.save(option);
            }
        });
    }

    @Override
    @Transactional
    public void cancelOrderBySeller(CancelOrderRequest cancelOrderRequest, HttpServletRequest request) {
        Member member = memberUtil.getMember(request).toModel();
        Seller seller = sellerRepository.findByMember(member);
        Order order = orderRepository.findByIdAndSeller(cancelOrderRequest.getOrderId(), seller);
        orderCancel(order);
    }

    @Override
    @Transactional
    public void processingOrder(ProcessingOrderRequest processingOrderRequest, HttpServletRequest request) {
        Member member = memberUtil.getMember(request).toModel();
        Seller seller = sellerRepository.findByMember(member);
        Order order = orderRepository.findByIdAndSeller(processingOrderRequest.getOrderId(), seller);
        order.processing();
        orderRepository.save(order);

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(order);
        orderItemList.forEach(orderItem -> {
            orderItem.processing();
            orderItem.order(order);
            orderItemRepository.save(orderItem);
        });
    }

    @Override
    @Transactional
    public void confirmOrder(ConfirmOrderRequest confirmOrderRequest, HttpServletRequest request) {
        Member member = memberUtil.getMember(request).toModel();
        Seller seller = sellerRepository.findByMember(member);
        Order order = orderRepository.findByIdAndSeller(confirmOrderRequest.getOrderId(), seller);
        order.confirm();
        orderRepository.save(order);

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(order);
        orderItemList.forEach(orderItem -> {
            orderItem.confirm();
            orderItem.order(order);
            orderItemRepository.save(orderItem);
        });
    }
}