package com.juno.appling.product.application;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.domain.SellerRepository;
import com.juno.appling.product.domain.*;
import com.juno.appling.product.dto.request.AddViewCntRequest;
import com.juno.appling.product.dto.request.OptionRequest;
import com.juno.appling.product.dto.request.ProductRequest;
import com.juno.appling.product.dto.request.PutProductRequest;
import com.juno.appling.product.dto.response.*;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
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
    private final OptionRepository optionRepository;

    @Transactional
    public ProductResponse postProduct(ProductRequest productRequest, HttpServletRequest request) {
        Long categoryId = productRequest.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );
        ProductType type = ProductType.valueOf(productRequest.getType().toUpperCase(Locale.ROOT));

        Member member = getMember(request);
        Seller seller = sellerRepository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 판매자입니다."));
        Product product = productRepository.save(Product.of(seller, category, productRequest));


        if(type == ProductType.NORMAL) {
            return new ProductResponse(product);
        }

        List<Option> optionList = new LinkedList<>();
        for (int i = 0; i < productRequest.getOptionList().size(); i++) {
            Option option = Option.of(product, productRequest.getOptionList().get(i));
            optionList.add(option);
            optionRepository.save(option);
        }

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
        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepository.findAll(pageable, search, productStatusOfEnums,
            category, sellerId);

        return new ProductListResponse(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public ProductBasketListResponse getProductBasket(List<Long> productIdList) {
        List<ProductResponse> productList = productCustomRepository.findAllByIdList(productIdList);
        return new ProductBasketListResponse(productList);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품번호 입니다.")
        );

        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse putProduct(PutProductRequest putProductRequest) {
        Long targetProductId = putProductRequest.getProductId();

        Long categoryId = putProductRequest.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        Product product = productRepository.findById(targetProductId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        if(product.getType() == ProductType.OPTION) {
            // optionList update
            for (OptionRequest optionRequest : putProductRequest.getOptionList()) {
                Option option = optionRepository.findById(optionRequest.getOptionId()).orElseThrow(() ->
                    new IllegalArgumentException("유효하지 않은 옵션입니다.")
                );
                option.put(optionRequest);
            }
        }

        product.put(putProductRequest);
        product.putCategory(category);

        return new ProductResponse(product);
    }

    public ProductListResponse getProductListBySeller(Pageable pageable, String search, String status,
                                                      Long categoryId, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepository.findAll(pageable, search, productStatusOfEnums,
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
