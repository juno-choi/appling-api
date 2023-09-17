package com.juno.appling.product.presentation;

import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.base.ResultCode;
import com.juno.appling.product.application.ProductService;
import com.juno.appling.product.dto.request.AddViewCntRequest;
import com.juno.appling.product.dto.response.CategoryListResponse;
import com.juno.appling.product.dto.response.ProductBasketListResponse;
import com.juno.appling.product.dto.response.ProductListResponse;
import com.juno.appling.product.dto.response.ProductResponse;
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
        return ResponseEntity.ok(Api.<ProductListResponse>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProductList(pageable, search, status, categoryId, sellerId))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Api<ProductResponse>> getProduct(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(Api.<ProductResponse>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProduct(id))
                .build());
    }


    @GetMapping("/category")
    public ResponseEntity<Api<CategoryListResponse>> getCategoryList() {
        return ResponseEntity.ok(Api.<CategoryListResponse>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getCategoryList())
                .build());
    }

    @PatchMapping("/cnt")
    public ResponseEntity<Api<MessageVo>> addViewCnt(
            @RequestBody @Validated AddViewCntRequest addViewCntRequest, BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.addViewCnt(addViewCntRequest))
                .build());
    }

    @GetMapping("/basket")
    public ResponseEntity<Api<ProductBasketListResponse>> getProductBasket(@RequestParam(name = "product_id") List<Long> productIdList) {
        return ResponseEntity.ok(Api.<ProductBasketListResponse>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProductBasket(productIdList))
                .build());
    }
}
