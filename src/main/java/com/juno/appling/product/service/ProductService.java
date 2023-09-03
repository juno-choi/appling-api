package com.juno.appling.product.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.entity.Member;
import com.juno.appling.member.domain.entity.Seller;
import com.juno.appling.member.repository.MemberRepository;
import com.juno.appling.member.repository.SellerRepository;
import com.juno.appling.product.domain.dto.AddViewCntDto;
import com.juno.appling.product.domain.dto.ProductDto;
import com.juno.appling.product.domain.dto.PutProductDto;
import com.juno.appling.product.domain.entity.Category;
import com.juno.appling.product.domain.entity.Product;
import com.juno.appling.product.domain.enums.Status;
import com.juno.appling.product.domain.vo.*;
import com.juno.appling.product.repository.CategoryRepository;
import com.juno.appling.product.repository.ProductCustomRepository;
import com.juno.appling.product.repository.ProductRepository;
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
    public ProductVo postProduct(ProductDto productDto, HttpServletRequest request) {
        Long categoryId = productDto.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        Member member = getMember(request);
        Seller seller = sellerRepository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 판매자입니다."));
        Product product = productRepository.save(Product.of(seller, category, productDto));
        return new ProductVo(product);
    }

    private Member getMember(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    public ProductListVo getProductList(Pageable pageable, String search, String status,
        Long categoryId, Long sellerId) {
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, statusOfEnums,
            category, sellerId);

        return new ProductListVo(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public ProductVo getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품번호 입니다.")
        );

        return new ProductVo(product);
    }

    @Transactional
    public ProductVo putProduct(PutProductDto putProductDto) {
        Long targetProductId = putProductDto.getId();

        Long categoryId = putProductDto.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        Product product = productRepository.findById(targetProductId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        product.put(putProductDto);
        product.putCategory(category);

        return new ProductVo(product);
    }

    public ProductListVo getProductListBySeller(Pageable pageable, String search, String status,
        Long categoryId, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, statusOfEnums,
            category, memberId);

        return new ProductListVo(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public CategoryListVo getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryVo> categoryVoList = new LinkedList<>();
        for (Category c : categoryList) {
            categoryVoList.add(
                new CategoryVo(c.getId(), c.getName(), c.getCreatedAt(), c.getModifiedAt()));
        }

        return new CategoryListVo(categoryVoList);
    }

    @Transactional
    public MessageVo addViewCnt(AddViewCntDto addViewCntDto) {
        Product product = productRepository.findById(addViewCntDto.getProductId()).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );
        product.addViewCnt();
        return new MessageVo("조회수 증가 성공");
    }
}
