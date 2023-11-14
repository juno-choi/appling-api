package com.juno.appling.order.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelOrderRequest {
    @JsonProperty("order_id")
    @NotNull(message = "order_id 비어있을 수 없습니다.")
    private Long orderId;
}
