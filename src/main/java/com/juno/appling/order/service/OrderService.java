package com.juno.appling.order.service;

import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.OrderInfoResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    PostTempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request);
    OrderInfoResponse getOrderInfo(Long orderId, HttpServletRequest request);
//    CompleteOrderResponse completeOrder(CompleteOrderRequest completeOrderRequest, HttpServletRequest request);
//    OrderResponse getOrderListBySeller(Pageable pageable, String search, String status, HttpServletRequest request);
}
