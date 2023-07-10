package com.juno.appling.domain.product.controller;

import com.juno.appling.config.base.Api;
import com.juno.appling.config.base.MessageVo;
import com.juno.appling.config.base.ResultCode;
import com.juno.appling.domain.product.dto.AddViewCntDto;
import com.juno.appling.domain.product.vo.CategoryListVo;
import com.juno.appling.domain.product.vo.ProductListVo;
import com.juno.appling.domain.product.vo.ProductVo;
import com.juno.appling.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api-prefix}/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListVo>> getProductList(@PageableDefault(size = 10, page = 0) Pageable pageable, @RequestParam(required = false, name = "search") String search, @RequestParam(required = false, name = "status", defaultValue = "normal") String status){
        return ResponseEntity.ok(Api.<ProductListVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProductList(pageable, search, status))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Api<ProductVo>> getProduct(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(Api.<ProductVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProduct(id))
                .build());
    }


    @GetMapping("/category")
    public ResponseEntity<Api<CategoryListVo>> getCategoryList(){
        return ResponseEntity.ok(Api.<CategoryListVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getCategoryList())
                .build());
    }

    @PatchMapping("/cnt")
    public ResponseEntity<Api<MessageVo>> addViewCnt(@RequestBody @Validated AddViewCntDto addViewCntDto, BindingResult bindingResult){
        return ResponseEntity.ok(Api.<MessageVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.addViewCnt(addViewCntDto))
                .build());
    }
}
