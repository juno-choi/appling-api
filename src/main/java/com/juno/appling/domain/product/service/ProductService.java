package com.juno.appling.domain.product.service;

import com.juno.appling.config.base.MessageVo;
import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.domain.product.dto.AddViewCntDto;
import com.juno.appling.domain.product.dto.PutProductDto;
import com.juno.appling.domain.product.dto.ProductDto;
import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.product.entity.Category;
import com.juno.appling.domain.product.entity.Product;
import com.juno.appling.domain.product.enums.Status;
import com.juno.appling.domain.product.vo.*;
import com.juno.appling.domain.member.repository.MemberRepository;
import com.juno.appling.domain.product.repository.CategoryRepository;
import com.juno.appling.domain.product.repository.ProductCustomRepository;
import com.juno.appling.domain.product.repository.ProductRepository;
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

    @Transactional
    public ProductVo postProduct(ProductDto productDto, HttpServletRequest request){
        Long categoryId = productDto.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        Member member = getMember(request);

        Product product = productRepository.save(Product.of(member, category, productDto));
        return ProductVo.productReturnVo(product);
    }

    private Member getMember(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));
        return member;
    }

    public ProductListVo getProductList(Pageable pageable, String search, String status){
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));

        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, statusOfEnums, 0L);

        return new ProductListVo(page.getTotalPages(), page.getTotalElements(), page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public ProductVo getProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품번호 입니다.")
        );

        return ProductVo.productReturnVo(product, new SellerVo(product.getMember().getId(), product.getMember().getEmail(), product.getMember().getNickname(), product.getMember().getName()));
    }

    @Transactional
    public ProductVo putProduct(PutProductDto putProductDto){
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

        return ProductVo.productReturnVo(product);
    }

    public ProductListVo getProductListBySeller(Pageable pageable, String search, String status, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Status statusOfEnums = Status.valueOf(status.toUpperCase(Locale.ROOT));
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, statusOfEnums, memberId);

        return new ProductListVo(page.getTotalPages(), page.getTotalElements(), page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    public CategoryListVo getCategoryList(){
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryVo> categoryVoList = new LinkedList<>();
        for(Category c : categoryList){
            categoryVoList.add(new CategoryVo(c.getId(), c.getName(), c.getCreatedAt(), c.getModifiedAt()));
        }

        return new CategoryListVo(categoryVoList);
    }

    @Transactional
    public MessageVo addViewCnt(AddViewCntDto addViewCntDto){
        Product product = productRepository.findById(addViewCntDto.getProductId()).orElseThrow(() ->
                new IllegalArgumentException("유효하지 않은 상품입니다.")
        );
        product.addViewCnt();
        return MessageVo.builder()
                .message("조회수 증가 성공")
                .build();
    }
}
