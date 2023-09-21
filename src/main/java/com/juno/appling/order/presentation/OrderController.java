package com.juno.appling.order.presentation;

import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.ResultCode;
import com.juno.appling.order.application.OrderService;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.TempOrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Api<TempOrderResponse>> postOrder(@RequestBody @Validated TempOrderRequest tempOrderRequest, HttpServletRequest request, BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        Api.<TempOrderResponse>builder()
                                .code(ResultCode.POST.code)
                                .message(ResultCode.POST.message)
                                .data(orderService.postTempOrder(tempOrderRequest, request))
                                .build()
                );
    }
}
