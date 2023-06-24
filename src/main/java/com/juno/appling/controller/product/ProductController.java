package com.juno.appling.controller.product;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.enums.ResultCode;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/seller/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Api<ProductVo>> postProduct(@RequestBody ProductDto productDto, HttpServletRequest request, BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Api.<ProductVo>builder()
                        .code(ResultCode.POST.code)
                        .message(ResultCode.POST.message)
                        .data(productService.postProduct(productDto, request))
                        .build());
    }
}
