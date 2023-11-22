package com.juno.appling.order.service;

import com.juno.appling.order.controller.request.CancelOrderRequest;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.OrderInfoResponse;
import com.juno.appling.order.controller.response.OrderListResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    PostTempOrderResponse postTempOrder(TempOrderRequest tempOrderRequest, HttpServletRequest request);
    OrderInfoResponse getOrderInfo(Long orderId, HttpServletRequest request);
    CompleteOrderResponse completeOrder(CompleteOrderRequest completeOrderRequest, HttpServletRequest request);
    OrderListResponse getOrderListBySeller(Pageable pageable, String search, String status, HttpServletRequest request);
//    Order getOrderDetailBySeller(Long orderId, HttpServletRequest request);

//    OrderResponse getOrderListByMember(Pageable pageable, String search, String status, HttpServletRequest request);
//
//    OrderVo getOrderDetailByMember(Long orderId, HttpServletRequest request);
//
    void cancelOrder(CancelOrderRequest cancelOrderRequest, HttpServletRequest request);
}
