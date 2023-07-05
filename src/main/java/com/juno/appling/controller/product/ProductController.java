package com.juno.appling.controller.product;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.enums.ResultCode;
import com.juno.appling.domain.vo.product.CategoryListVo;
import com.juno.appling.domain.vo.product.CategoryVo;
import com.juno.appling.domain.vo.product.ProductListVo;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api-prefix}/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListVo>> getProductList(@PageableDefault(size = 10, page = 0) Pageable pageable, @RequestParam(required = false, name = "search") String search){
        return ResponseEntity.ok(Api.<ProductListVo>builder()
                .code(ResultCode.SUCCESS.code)
                .message(ResultCode.SUCCESS.message)
                .data(productService.getProductList(pageable, search))
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
}
