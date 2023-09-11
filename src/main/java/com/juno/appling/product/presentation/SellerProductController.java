package com.juno.appling.product.presentation;

import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.ResultCode;
import com.juno.appling.product.dto.request.ProductRequest;
import com.juno.appling.product.dto.request.PutProductRequest;
import com.juno.appling.product.dto.response.ProductListResponse;
import com.juno.appling.product.dto.response.ProductResponse;
import com.juno.appling.product.application.ProductService;
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
@RequestMapping("${api-prefix}/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListResponse>> getProductList(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false, name = "search") String search,
        @RequestParam(required = false, name = "status", defaultValue = "normal") String status,
        @RequestParam(required = false, name = "category_id", defaultValue = "0") Long categoryId,
        HttpServletRequest request) {
        return ResponseEntity.ok(Api.<ProductListResponse>builder()
            .code(ResultCode.SUCCESS.code)
            .message(ResultCode.SUCCESS.message)
            .data(productService.getProductListBySeller(pageable, search, status, categoryId,
                request))
            .build());
    }

    @PostMapping
    public ResponseEntity<Api<ProductResponse>> postProduct(@RequestBody @Validated ProductRequest productRequest,
                                                            HttpServletRequest request, BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Api.<ProductResponse>builder()
                .code(ResultCode.POST.code)
                .message(ResultCode.POST.message)
                .data(productService.postProduct(productRequest, request))
                .build());
    }

    @PutMapping
    public ResponseEntity<Api<ProductResponse>> putProduct(
            @RequestBody @Validated PutProductRequest putProductRequest, BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<ProductResponse>builder()
            .code(ResultCode.SUCCESS.code)
            .message(ResultCode.SUCCESS.message)
            .data(productService.putProduct(putProductRequest))
            .build());
    }
}
