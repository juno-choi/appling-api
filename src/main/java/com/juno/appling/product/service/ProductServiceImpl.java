package com.juno.appling.product.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.infrastruceture.MemberRepository;
import com.juno.appling.member.infrastruceture.SellerRepository;
import com.juno.appling.product.controller.request.AddViewCntRequest;
import com.juno.appling.product.controller.request.OptionRequest;
import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.controller.response.CategoryListResponse;
import com.juno.appling.product.controller.response.CategoryResponse;
import com.juno.appling.product.controller.response.ProductBasketListResponse;
import com.juno.appling.product.controller.response.ProductListResponse;
import com.juno.appling.product.controller.response.ProductResponse;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.Option;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.OptionStatus;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.infrastructure.CategoryRepository;
import com.juno.appling.product.infrastructure.OptionRepository;
import com.juno.appling.product.infrastructure.ProductCustomRepositoryImpl;
import com.juno.appling.product.infrastructure.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductCustomRepositoryImpl productCustomRepositoryImpl;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final OptionRepository optionRepository;

    @Transactional
    @Override
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
        }
        optionRepository.saveAll(optionList);

        return new ProductResponse(product);
    }

    private Member getMember(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    @Override
    public ProductListResponse getProductList(Pageable pageable, String search, String status,
                                              Long categoryId, Long sellerId) {
        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search, productStatusOfEnums,
            category, sellerId);

        return new ProductListResponse(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    @Override
    public ProductBasketListResponse getProductBasket(List<Long> productIdList) {
        List<ProductResponse> productList = productCustomRepositoryImpl.findAllByIdList(productIdList);
        return new ProductBasketListResponse(productList);
    }

    @Override
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품번호 입니다.")
        );

        return new ProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse putProduct(PutProductRequest putProductRequest) {
        Long targetProductId = putProductRequest.getProductId();

        Long categoryId = putProductRequest.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        Product product = productRepository.findById(targetProductId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        product.put(putProductRequest);
        product.putCategory(category);

        if(product.getType() == ProductType.OPTION) {
            // optionList update
            List<Option> findOptionList = optionRepository.findByProductAndStatus(product, OptionStatus.NORMAL);
            List<OptionRequest> saveRequestOptionList = new LinkedList<>();

            for (OptionRequest optionRequest : putProductRequest.getOptionList()) {
                Long requestOptionId = Optional.ofNullable(optionRequest.getOptionId()).orElse(0L);
                Optional<Option> optionalOption = findOptionList.stream().filter(option -> option.getId().equals(requestOptionId)).findFirst();

                if(optionalOption.isPresent()){
                    Option option = optionalOption.get();
                    option.put(optionRequest);
                } else {
                    saveRequestOptionList.add(optionRequest);
                }
            }

            if(!saveRequestOptionList.isEmpty()) {
                List<Option> options = Option.ofList(product, saveRequestOptionList);
                optionRepository.saveAll(options);
                product.addAllOptionsList(options);
            }
        }

        return new ProductResponse(product);
    }

    @Override
    public ProductListResponse getProductListBySeller(Pageable pageable, String search, String status,
                                                      Long categoryId, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search, productStatusOfEnums,
            category, memberId);

        return new ProductListResponse(page.getTotalPages(), page.getTotalElements(),
            page.getNumberOfElements(), page.isLast(), page.isEmpty(), page.getContent());
    }

    @Override
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
    @Override
    public MessageVo addViewCnt(AddViewCntRequest addViewCntRequest) {
        Product product = productRepository.findById(addViewCntRequest.getProductId()).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );
        product.addViewCnt();
        return new MessageVo("조회수 증가 성공");
    }
}
