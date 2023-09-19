package com.juno.appling.order.application;

import static org.junit.jupiter.api.Assertions.*;

import com.juno.appling.BaseTest;
import com.juno.appling.order.dto.request.TempOrder;
import com.juno.appling.order.dto.request.TempOrderRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceTest extends BaseTest {
    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess(){

    }
}