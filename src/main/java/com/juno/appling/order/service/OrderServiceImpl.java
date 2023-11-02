package com.juno.appling.order.service;

import com.github.dockerjava.api.exception.UnauthorizedException;
import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.entity.SellerEntity;
import com.juno.appling.member.repository.SellerJpaRepository;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.OrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.controller.response.TempOrderResponse;
import com.juno.appling.order.domain.entity.DeliveryEntity;
import com.juno.appling.order.domain.entity.OrderEntity;
import com.juno.appling.order.domain.entity.OrderItemEntity;
import com.juno.appling.order.domain.vo.OrderVo;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.order.repository.DeliveryJpaRepository;
import com.juno.appling.order.repository.OrderCustomJpaRepositoryImpl;
import com.juno.appling.order.repository.OrderItemJpaRepository;
import com.juno.appling.order.repository.OrderJpaRepository;
import com.juno.appling.product.domain.entity.OptionEntity;
import com.juno.appling.product.domain.entity.ProductEntity;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.repository.ProductJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{
    private final ProductJpaRepository productJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final DeliveryJpaRepository deliveryJpaRepository;
    private final MemberUtil memberUtil;
    private final SellerJpaRepository sellerJpaRepository;
    private final OrderCustomJpaRepositoryImpl orderCustomRepositoryImpl;

    @Transactional
    @Override
    public PostTempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request){
        MemberEntity memberEntity = memberUtil.getMember(request);

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

        List<ProductEntity> productEntityList = productJpaRepository.findAllById(requestProductIdList);
        productEntityList = productEntityList.stream().sorted((p1, p2) -> p2.getPrice() - p1.getPrice()).collect(Collectors.toList());

        if((requestOrderProductList.size() != productEntityList.size()) || productEntityList.size() == 0){
            throw new IllegalArgumentException("유효하지 않은 상품이 존재합니다.");
        }

        sb.append(productEntityList.get(0).getMainTitle());
        if(productEntityList.size() > 1){
            sb.append(" 외 ");
            sb.append(productEntityList.size() -1);
            sb.append("개");
        }
        OrderEntity saveOrderEntity = orderJpaRepository.save(OrderEntity.of(memberEntity, sb.toString()));

        // 주문 상품 등록
        Map<Long, TempOrderDto> eaMap = new HashMap<>();
        for(TempOrderDto o : requestOrderProductList){
            eaMap.put(o.getProductId(), o);
        }

        for(ProductEntity p : productEntityList){
            if(p.getStatus() != ProductStatus.NORMAL){
                throw new IllegalArgumentException("상품 상태가 유효하지 않습니다.");
            }

            Long optionId = eaMap.get(p.getId()).getOptionId();

            // 옵션이 없을 경우 exception 처리
            OptionEntity optionEntity = p.getType() == ProductType.OPTION ? p.getOptionList().stream().filter(o -> o.getId().equals(optionId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("optionId가 유효하지 않습니다.")) : null;

            // 재고 확인
            int ea = eaMap.get(p.getId()).getEa();
            int productEa = p.getType() == ProductType.OPTION ? optionEntity.getEa() : p.getEa();
            checkEa(p.getMainTitle(), productEa, ea);

            OrderItemEntity orderItemEntity = orderItemJpaRepository.save(
                OrderItemEntity.of(saveOrderEntity, p, optionEntity, ea));
            saveOrderEntity.getOrderItemList().add(orderItemEntity);
        }

        return PostTempOrderResponse.builder().orderId(saveOrderEntity.getId()).build();
    }

    private void checkEa(String productName, int productEa, int orderEa) {
        if(productEa < orderEa) {
            throw  new IllegalArgumentException(String.format("[%s]상품의 재고가 부족합니다! 남은 재고 = %s개", productName, productEa));
        }
    }

    @Override
    public TempOrderResponse getTempOrder(Long orderId, HttpServletRequest request){
        /**
         * order id와 member 정보로 임시 정보를 불러옴
         */
        OrderEntity orderEntity = checkOrder(request, orderId);

        return TempOrderResponse.from(orderEntity);
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
        Long orderId = completeOrderRequest.getOrderId();
        OrderEntity orderEntity = checkOrder(request, orderId);

        // 재고 확인
        List<OrderItemEntity> orderItemEntityList = orderEntity.getOrderItemList();
        for(OrderItemEntity oi : orderItemEntityList){
            ProductEntity productEntity = oi.getProduct();

            // type 체크
            int orderEa = oi.getEa();
            ProductType productType = productEntity.getType();
            int productEa = 0;
            OptionEntity optionEntity = null;
            if(productType == ProductType.OPTION) {
                optionEntity = Optional.ofNullable(oi.getOption()).orElseThrow(() -> new IllegalArgumentException("요청한 주문의 optionId가 유효하지 않습니다."));
                OptionEntity constOptionEntity = optionEntity;
                productEa = productEntity.getOptionList().stream().filter(o -> o.getId().equals(
                        constOptionEntity.getId()))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("optionId가 유효하지 않습니다.")).getEa();
            } else {
                productEa = productEntity.getEa();
            }

            checkEa(productEntity.getMainTitle(), productEa, orderEa);

            // TODO 일반 상품은 minus 옵션 상품은 minusOption
            productEntity.minusEa(orderEa, optionEntity);
        }

        // 배송 정보 등록
        for(OrderItemEntity oi : orderItemEntityList){
            deliveryJpaRepository.save(DeliveryEntity.of(orderEntity, oi, completeOrderRequest));
        }

        orderEntity.statusComplete();

        LocalDateTime createdAt = orderEntity.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String orderNumber = String.format("ORDER-%s-%s", createdAt.format(formatter), orderId);
        orderEntity.orderNumber(orderNumber);

        return CompleteOrderResponse.from(orderEntity);
    }

    /**
     * Checks the order with the given order ID against the member's ID in the request.
     *
     * @param  request   the HttpServletRequest object containing the request information
     * @param  orderId   the ID of the order to be checked
     * @return           the Order object if the order is valid
     * @throws IllegalArgumentException if the order ID is invalid or if the member ID in the request
     *                                  does not match the member ID in the order or if the order
     *                                  status is not TEMP
     */
    private OrderEntity checkOrder(HttpServletRequest request, Long orderId) {
        MemberEntity memberEntity = memberUtil.getMember(request);
        OrderEntity orderEntity = orderJpaRepository.findById(orderId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 주문 번호입니다.")
        );

        if(memberEntity.getId() != orderEntity.getMemberEntity().getId()) {
            log.info("[getOrder] 유저가 주문한 번호가 아님! 요청한 user_id = {} , order_id = {}", memberEntity.getId(), orderEntity.getId());
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }

        if(orderEntity.getStatus() != OrderStatus.TEMP) {
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }

        return orderEntity;
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