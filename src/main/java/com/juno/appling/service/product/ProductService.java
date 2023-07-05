package com.juno.appling.service.product;

import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.product.PutProductDto;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.product.Category;
import com.juno.appling.domain.entity.product.Product;
import com.juno.appling.domain.vo.product.CategoryVo;
import com.juno.appling.domain.vo.product.ProductListVo;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.domain.vo.product.SellerVo;
import com.juno.appling.repository.member.MemberRepository;
import com.juno.appling.repository.product.CategoryRepository;
import com.juno.appling.repository.product.ProductCustomRepository;
import com.juno.appling.repository.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ProductListVo getProductList(Pageable pageable, String search){
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, 0L);

        return ProductListVo.builder()
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .last(page.isLast())
                .empty(page.isLast())
                .list(page.getContent())
                .build();
    }

    public ProductVo getProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new IllegalArgumentException("유효하지 않은 상품번호 입니다.");
        });

        return ProductVo.builder()
                .id(product.getId())
                .mainTitle(product.getMainTitle())
                .mainExplanation(product.getMainExplanation())
                .productMainExplanation(product.getProductMainExplanation())
                .productSubExplanation(product.getProductSubExplanation())
                .purchaseInquiry(product.getPurchaseInquiry())
                .producer(product.getProducer())
                .origin(product.getOrigin())
                .originPrice(product.getOriginPrice())
                .price(product.getPrice())
                .mainImage(product.getMainImage())
                .image1(product.getImage1())
                .image2(product.getImage2())
                .image3(product.getImage3())
                .createAt(product.getCreateAt())
                .modifiedAt(product.getModifiedAt())
                .seller(SellerVo.builder()
                        .name(product.getMember().getName())
                        .email(product.getMember().getEmail())
                        .nickname(product.getMember().getEmail())
                        .memberId(product.getMember().getId())
                        .build())
                .category(CategoryVo.builder()
                        .categoryId(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .createdAt(product.getCategory().getCreatedAt())
                        .modifiedAt(product.getCategory().getModifiedAt())
                        .build())
                .build();
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

    public ProductListVo getProductListBySeller(Pageable pageable, String search, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Page<ProductVo> page = productCustomRepository.findAll(pageable, search, memberId);

        return ProductListVo.builder()
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .last(page.isLast())
                .empty(page.isLast())
                .list(page.getContent())
                .build();
    }
}
