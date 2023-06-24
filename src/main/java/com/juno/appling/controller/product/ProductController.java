package com.juno.appling.controller.product;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.enums.ResultCode;
import com.juno.appling.domain.vo.product.ProductListVo;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.service.product.ProductService;
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
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Api<ProductVo>> postProduct(@RequestBody @Validated ProductDto productDto, HttpServletRequest request, BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Api.<ProductVo>builder()
                        .code(ResultCode.POST.code)
                        .message(ResultCode.POST.message)
                        .data(productService.postProduct(productDto, request))
                        .build());
    }

    @GetMapping
    public ResponseEntity<Api<ProductListVo>> postProduct(@PageableDefault(size = 10) Pageable pageable, @RequestParam(name = "search") String search){
        return ResponseEntity.ok(Api.<ProductListVo>builder()
                        .code(ResultCode.SUCCESS.code)
                        .message(ResultCode.SUCCESS.message)
                        .data(productService.getProductList(pageable, search))
                        .build());
    }
}
