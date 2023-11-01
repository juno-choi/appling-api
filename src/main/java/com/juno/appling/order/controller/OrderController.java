package com.juno.appling.order.controller;

import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.ResultCode;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.OrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.controller.response.TempOrderResponse;
import com.juno.appling.order.service.OrderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderServiceImpl;

    @PostMapping
    public ResponseEntity<Api<PostTempOrderResponse>> postOrder(@RequestBody @Validated TempOrderRequest tempOrderRequest, HttpServletRequest request, BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    new Api<>(ResultCode.POST.code, ResultCode.POST.message, orderServiceImpl.postTempOrder(tempOrderRequest, request))
                );
    }

    @GetMapping("/temp/{order_id}")
    public ResponseEntity<Api<TempOrderResponse>> getTempOrder(@PathVariable(name = "order_id") Long orderId, HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderServiceImpl.getTempOrder(orderId, request))
        );
    }

    @PatchMapping("/complete")
    public ResponseEntity<Api<CompleteOrderResponse>> completeOrder(@RequestBody @Validated CompleteOrderRequest completeOrderRequest, HttpServletRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderServiceImpl.completeOrder(completeOrderRequest, request))
        );
    }

    @GetMapping("/seller")
    public ResponseEntity<Api<OrderResponse>> getOrderBySeller(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false, name = "search") String search,
        @RequestParam(required = false, name = "status", defaultValue = "complete") String status,
        HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, orderServiceImpl.getOrderListBySeller(pageable, search, status, request))
        );
    }
}
