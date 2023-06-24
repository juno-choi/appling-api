package com.juno.appling.controller.product;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.enums.ResultCode;
import com.juno.appling.domain.vo.product.ProductListVo;
import com.juno.appling.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListVo>> postProduct(@PageableDefault(size = 10) Pageable pageable, @RequestParam(name = "search") String search){
        return ResponseEntity.ok(Api.<ProductListVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProductList(pageable, search))
                .build());
    }
}
