package com.juno.appling.controller.product;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.dto.product.PutProductDto;
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
public class SellerProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListVo>> getProductList(@PageableDefault(size = 10, page = 0) Pageable pageable, @RequestParam(required = false, name = "search") String search, HttpServletRequest request){
        return ResponseEntity.ok(Api.<ProductListVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProductListBySeller(pageable, search, request))
                .build());
    }

    @PostMapping
    public ResponseEntity<Api<ProductVo>> postProduct(@RequestBody @Validated ProductDto productDto, HttpServletRequest request, BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Api.<ProductVo>builder()
                        .code(ResultCode.POST.code)
                        .message(ResultCode.POST.message)
                        .data(productService.postProduct(productDto, request))
                        .build());
    }

    @PutMapping
    public ResponseEntity<Api<ProductVo>> putProduct(@RequestBody @Validated PutProductDto putProductDto, BindingResult bindingResult){
        return ResponseEntity.ok(Api.<ProductVo>builder()
                        .code(ResultCode.SUCCESS.code)
                        .message(ResultCode.SUCCESS.message)
                        .data(productService.putProduct(putProductDto))
                        .build());
    }
}
