package com.juno.appling.order.service;

import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    PostTempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request);
    OrderInfoResponse getOrderInfo(Long orderId, HttpServletRequest request);
    CompleteOrderResponse completeOrder(CompleteOrderRequest completeOrderRequest, HttpServletRequest request);
    OrderResponse getOrderListBySeller(Pageable pageable, String search, String status, HttpServletRequest request);

    OrderResponse getOrderListByMember(Pageable pageable, String search, String status, HttpServletRequest request);

    OrderDetailResponse getOrderDetail(Long orderId, HttpServletRequest request);
}
