package com.juno.appling.product.application;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.SellerRepository;
import com.juno.appling.product.dto.request.AddViewCntRequest;
import com.juno.appling.product.dto.request.ProductRequest;
import com.juno.appling.product.dto.request.PutProductRequest;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.Status;
import com.juno.appling.product.domain.CategoryRepository;
import com.juno.appling.product.domain.ProductCustomRepository;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.dto.response.CategoryListResponse;
import com.juno.appling.product.dto.response.CategoryResponse;
import com.juno.appling.product.dto.response.ProductListResponse;
import com.juno.appling.product.dto.response.ProductResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    @Transactional
    public ProductResponse postProduct(ProductRequest productRequest, HttpServletRequest request) {
        Long categoryId = productRequest.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        Member member = getMember(request);
        Seller seller = sellerRepository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 판매자입니다."));
        Product product = productRepository.save(Product.of(seller, category, productRequest));
        return new ProductResponse(product);
    }

    private Member getMember(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    public ProductListResponse getProductList(Pageable pageable, String search, String status,
                                              Long categoryId, Long sellerId) {
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepository.findAll(pageable, search, statusOfEnums,
            category, sellerId);

        return new ProductListResponse(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품번호 입니다.")
        );

        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse putProduct(PutProductRequest putProductRequest) {
        Long targetProductId = putProductRequest.getId();

        Long categoryId = putProductRequest.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        Product product = productRepository.findById(targetProductId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        product.put(putProductRequest);
        product.putCategory(category);

        return new ProductResponse(product);
    }

    public ProductListResponse getProductListBySeller(Pageable pageable, String search, String status,
                                                      Long categoryId, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepository.findAll(pageable, search, statusOfEnums,
            category, memberId);

        return new ProductListResponse(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public CategoryListResponse getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryResponse> categoryResponseList = new LinkedList<>();
        for (Category c : categoryList) {
            categoryResponseList.add(
                new CategoryResponse(c.getId(), c.getName(), c.getCreatedAt(), c.getModifiedAt()));
        }

        return new CategoryListResponse(categoryResponseList);
    }

    @Transactional
    public MessageVo addViewCnt(AddViewCntRequest addViewCntRequest) {
        Product product = productRepository.findById(addViewCntRequest.getProductId()).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );
        product.addViewCnt();
        return new MessageVo("조회수 증가 성공");
    }
}
