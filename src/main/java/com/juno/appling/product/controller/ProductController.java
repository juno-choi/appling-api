package com.juno.appling.product.controller;

import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.base.ResultCode;
import com.juno.appling.product.service.ProductService;
import com.juno.appling.product.controller.request.AddViewCntRequest;
import com.juno.appling.product.controller.response.CategoryListResponse;
import com.juno.appling.product.controller.response.ProductBasketListResponse;
import com.juno.appling.product.controller.response.ProductListResponse;
import com.juno.appling.product.controller.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api-prefix}/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListResponse>> getProductList(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @RequestParam(required = false, name = "search") String search,
            @RequestParam(required = false, name = "status", defaultValue = "normal") String status,
            @RequestParam(required = false, name = "category_id", defaultValue = "0") Long categoryId,
            @RequestParam(required = false, name = "seller_id", defaultValue = "0") Long sellerId) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, productService.getProductList(pageable, search, status, categoryId, sellerId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Api<ProductResponse>> getProduct(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, productService.getProduct(id))
        );
    }


    @GetMapping("/category")
    public ResponseEntity<Api<CategoryListResponse>> getCategoryList() {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, productService.getCategoryList())
        );
    }

    @PatchMapping("/cnt")
    public ResponseEntity<Api<MessageVo>> addViewCnt(
            @RequestBody @Validated AddViewCntRequest addViewCntRequest, BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, productService.addViewCnt(addViewCntRequest))
        );
    }

    @GetMapping("/basket")
    public ResponseEntity<Api<ProductBasketListResponse>> getProductBasket(@RequestParam(name = "product_id") List<Long> productIdList) {
        return ResponseEntity.ok(
            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, productService.getProductBasket(productIdList))
        );
    }
}
