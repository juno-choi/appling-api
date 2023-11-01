package com.juno.appling.product.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.product.controller.request.AddViewCntRequest;
import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.controller.response.CategoryListResponse;
import com.juno.appling.product.controller.response.ProductBasketListResponse;
import com.juno.appling.product.controller.response.ProductListResponse;
import com.juno.appling.product.controller.response.ProductResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse postProduct(ProductRequest productRequest, HttpServletRequest request);
    ProductListResponse getProductList(Pageable pageable, String search, String status,
        Long categoryId, Long sellerId);
    ProductBasketListResponse getProductBasket(List<Long> productIdList);
    ProductResponse getProduct(Long id);
    ProductResponse putProduct(PutProductRequest putProductRequest);
    ProductListResponse getProductListBySeller(Pageable pageable, String search, String status,
        Long categoryId, HttpServletRequest request);
    CategoryListResponse getCategoryList();
    MessageVo addViewCnt(AddViewCntRequest addViewCntRequest);
}

