package com.juno.appling.controller.product;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.enums.ResultCode;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/seller/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Api<ProductVo>> postProduct(
            HttpServletRequest request
    ){
        return null;
    }
}
