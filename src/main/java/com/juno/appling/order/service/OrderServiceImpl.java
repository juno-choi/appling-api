package com.juno.appling.order.service;

import com.github.dockerjava.api.exception.UnauthorizedException;
import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.infrastruceture.SellerRepository;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.OrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.controller.response.TempOrderResponse;
import com.juno.appling.order.domain.Delivery;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.domain.OrderItem;
import com.juno.appling.order.domain.vo.OrderVo;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.order.infrastructure.DeliveryRepository;
import com.juno.appling.order.infrastructure.OrderCustomRepositoryImpl;
import com.juno.appling.order.infrastructure.OrderItemRepository;
import com.juno.appling.order.infrastructure.OrderRepository;
import com.juno.appling.product.domain.Option;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.infrastructure.ProductRepository;
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
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final MemberUtil memberUtil;
    private final SellerRepository sellerRepository;
    private final OrderCustomRepositoryImpl orderCustomRepositoryImpl;

    @Transactional
    @Override
    public PostTempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request){
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
        Map<Long, TempOrderDto> eaMap = new HashMap<>();
        for(TempOrderDto o : requestOrderProductList){
            eaMap.put(o.getProductId(), o);
        }

        for(Product p : productList){
            if(p.getStatus() != ProductStatus.NORMAL){
                throw new IllegalArgumentException("상품 상태가 유효하지 않습니다.");
            }

            Long optionId = eaMap.get(p.getId()).getOptionId();

            // 옵션이 없을 경우 exception 처리
            Option option = p.getType() == ProductType.OPTION ? p.getOptionList().stream().filter(o -> o.getId().equals(optionId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("optionId가 유효하지 않습니다.")) : null;

            // 재고 확인
            int ea = eaMap.get(p.getId()).getEa();
            int productEa = p.getType() == ProductType.OPTION ? option.getEa() : p.getEa();
            checkEa(p.getMainTitle(), productEa, ea);

            OrderItem orderItem = orderItemRepository.save(OrderItem.of(saveOrder, p, option, ea));
            saveOrder.getOrderItemList().add(orderItem);
        }

        return PostTempOrderResponse.builder().orderId(saveOrder.getId()).build();
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
        Order order = checkOrder(request, orderId);

        return TempOrderResponse.from(order);
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
        Order order = checkOrder(request, orderId);

        // 재고 확인
        List<OrderItem> orderItemList = order.getOrderItemList();
        for(OrderItem oi : orderItemList){
            Product product = oi.getProduct();

            // type 체크
            int orderEa = oi.getEa();
            ProductType productType = product.getType();
            int productEa = 0;
            Option option = null;
            if(productType == ProductType.OPTION) {
                option = Optional.ofNullable(oi.getOption()).orElseThrow(() -> new IllegalArgumentException("요청한 주문의 optionId가 유효하지 않습니다."));
                Option constOption = option;
                productEa = product.getOptionList().stream().filter(o -> o.getId().equals(constOption.getId()))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("optionId가 유효하지 않습니다.")).getEa();
            } else {
                productEa = product.getEa();
            }

            checkEa(product.getMainTitle(), productEa, orderEa);

            // TODO 일반 상품은 minus 옵션 상품은 minusOption
            product.minusEa(orderEa, option);
        }

        // 배송 정보 등록
        for(OrderItem oi : orderItemList){
            deliveryRepository.save(Delivery.of(order, oi, completeOrderRequest));
        }

        order.statusComplete();

        LocalDateTime createdAt = order.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String orderNumber = String.format("ORDER-%s-%s", createdAt.format(formatter), orderId);
        order.orderNumber(orderNumber);

        return CompleteOrderResponse.from(order);
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
    private Order checkOrder(HttpServletRequest request, Long orderId) {
        Member member = memberUtil.getMember(request);
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 주문 번호입니다.")
        );

        if(member.getId() != order.getMember().getId()) {
            log.info("[getOrder] 유저가 주문한 번호가 아님! 요청한 user_id = {} , order_id = {}", member.getId(), order.getId());
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }

        if(order.getStatus() != OrderStatus.TEMP) {
            throw new IllegalArgumentException("유효하지 않은 주문입니다.");
        }

        return order;
    }

    @Override
    public OrderResponse getOrderListBySeller(Pageable pageable, String search, String status, HttpServletRequest request) {
        Member member = memberUtil.getMember(request);
        Seller seller = sellerRepository.findByMember(member)
                .orElseThrow(() -> new UnauthorizedException("잘못된 접근입니다."));

        /**
         * 1. order item 중 seller product가 있는 리스트 불러오기
         * 2. response data 만들기
         */
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));

        Page<OrderVo> orderList = orderCustomRepositoryImpl.findAllBySeller(pageable, search, orderStatus, seller);

        return OrderResponse.from(orderList);
    }
}