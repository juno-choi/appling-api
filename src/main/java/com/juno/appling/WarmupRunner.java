package com.juno.appling;

import com.juno.appling.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarmupRunner implements ApplicationListener<ApplicationReadyEvent> {

    private final ProductService productService;

    public static boolean isWarmup = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // warmup
        Pageable pageable = Pageable.ofSize(10);
        productService.getProductList(pageable, "", "normal", 0L, 0L);
        isWarmup = true;
    }
}
