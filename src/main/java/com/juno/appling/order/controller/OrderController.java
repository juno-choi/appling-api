package com.juno.appling.order.controller;

import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.ResultCode;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.OrderInfoResponse;
import com.juno.appling.order.controller.response.OrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.controller.vo.OrderVo;
import com.juno.appling.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api-prefix}/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Api<PostTempOrderResponse>> postOrder(@RequestBody @Validated TempOrderRequest tempOrderRequest, HttpServletRequest request, BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    new Api<>(ResultCode.POST.code, ResultCode.POST.message, orderService.postTempOrder(tempOrderRequest, request))
                );
    }

    @GetMapping("/temp/{order_id}")
    public ResponseEntity<Api<OrderInfoResponse>> getOrderInfo(@PathVariable(name = "order_id") Long orderId, HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderService.getOrderInfo(orderId, request))
        );
    }

    @PatchMapping("/complete")
    public ResponseEntity<Api<CompleteOrderResponse>> completeOrder(@RequestBody @Validated CompleteOrderRequest completeOrderRequest, HttpServletRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderService.completeOrder(completeOrderRequest, request))
        );
    }

    @GetMapping("/seller")
    public ResponseEntity<Api<OrderResponse>> getOrderBySeller(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false, name = "search") String search,
        @RequestParam(required = false, name = "status", defaultValue = "complete") String status,
        HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderService.getOrderListBySeller(pageable, search, status, request))
        );
    }

    @GetMapping("/seller/{order_id}")
    public ResponseEntity<Api<OrderVo>> getOrderDetailBySeller(@PathVariable (name = "order_id") Long orderId, HttpServletRequest request) {
        return ResponseEntity.ok(
                new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderService.getOrderDetailBySeller(orderId, request))
        );
    }

    @GetMapping("/member")
    public ResponseEntity<Api<OrderResponse>> getOrderByMember(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @RequestParam(required = false, name = "search") String search,
            @RequestParam(required = false, name = "status", defaultValue = "complete") String status,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderService.getOrderListByMember(pageable, search, status, request))
        );
    }

    @GetMapping("/member/{order_id}")
    public ResponseEntity<Api<OrderVo>> getOrderDetailByMember(@PathVariable (name = "order_id") Long orderId, HttpServletRequest request) {
        return ResponseEntity.ok(
                new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderService.getOrderDetailByMember(orderId, request))
        );
    }
}
