package com.juno.appling.product.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.member.port.MemberJpaRepository;
import com.juno.appling.product.port.SellerJpaRepository;
import com.juno.appling.product.controller.request.AddViewCntRequest;
import com.juno.appling.product.controller.request.OptionRequest;
import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.controller.response.CategoryListResponse;
import com.juno.appling.product.controller.response.CategoryResponse;
import com.juno.appling.product.controller.response.ProductBasketListResponse;
import com.juno.appling.product.controller.response.ProductListResponse;
import com.juno.appling.product.controller.response.ProductResponse;
import com.juno.appling.product.domain.entity.CategoryEntity;
import com.juno.appling.product.domain.entity.OptionEntity;
import com.juno.appling.product.domain.entity.ProductEntity;
import com.juno.appling.product.enums.OptionStatus;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.port.CategoryJpaRepository;
import com.juno.appling.product.port.OptionJpaRepository;
import com.juno.appling.product.port.ProductCustomJpaRepositoryImpl;
import com.juno.appling.product.port.ProductJpaRepository;
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

    private final ProductJpaRepository productJpaRepository;
    private final ProductCustomJpaRepositoryImpl productCustomRepositoryImpl;
    private final MemberJpaRepository memberJpaRepository;
    private final TokenProvider tokenProvider;
    private final CategoryJpaRepository categoryJpaRepository;
    private final SellerJpaRepository sellerJpaRepository;
    private final OptionJpaRepository optionJpaRepository;

    @Transactional
    @Override
    public ProductResponse postProduct(ProductRequest productRequest, HttpServletRequest request) {
        Long categoryId = productRequest.getCategoryId();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );
        ProductType type = ProductType.valueOf(productRequest.getType().toUpperCase(Locale.ROOT));

        MemberEntity memberEntity = getMember(request);
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 판매자입니다."));
        ProductEntity productEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));


        if(type == ProductType.NORMAL) {
            return ProductResponse.from(productEntity);
        }

        List<OptionEntity> optionEntityList = new LinkedList<>();
        for (int i = 0; i < productRequest.getOptionList().size(); i++) {
            OptionEntity optionEntity = OptionEntity.of(
                productEntity, productRequest.getOptionList().get(i));
            optionEntityList.add(optionEntity);
        }
        optionJpaRepository.saveAll(optionEntityList);

        return ProductResponse.from(productEntity);
    }

    private MemberEntity getMember(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        return memberJpaRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    @Override
    public ProductListResponse getProductList(Pageable pageable, String search, String status,
                                              Long categoryId, Long sellerId) {
        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search, productStatusOfEnums,
            categoryEntity, sellerId);

        return ProductListResponse.from(page);
    }

    @Override
    public ProductBasketListResponse getProductBasket(List<Long> productIdList) {
        List<ProductResponse> productList = productCustomRepositoryImpl.findAllByIdList(productIdList);
        return ProductBasketListResponse.builder().basketList(productList).build();
    }

    @Override
    public ProductResponse getProduct(Long id) {
        ProductEntity productEntity = productJpaRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품번호 입니다.")
        );

        return ProductResponse.from(productEntity);
    }

    @Transactional
    @Override
    public ProductResponse putProduct(PutProductRequest putProductRequest) {
        Long targetProductId = putProductRequest.getProductId();

        Long categoryId = putProductRequest.getCategoryId();

        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 카테고리입니다.")
        );

        ProductEntity productEntity = productJpaRepository.findById(targetProductId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );

        productEntity.put(putProductRequest);
        productEntity.putCategory(categoryEntity);

        if(productEntity.getType() == ProductType.OPTION) {
            // optionList update
            List<OptionEntity> findOptionListEntity = optionJpaRepository.findByProductAndStatus(
                productEntity, OptionStatus.NORMAL);
            List<OptionRequest> saveRequestOptionList = new LinkedList<>();

            for (OptionRequest optionRequest : putProductRequest.getOptionList()) {
                Long requestOptionId = Optional.ofNullable(optionRequest.getOptionId()).orElse(0L);
                Optional<OptionEntity> optionalOption = findOptionListEntity.stream().filter(option -> option.getId().equals(requestOptionId)).findFirst();

                if(optionalOption.isPresent()){
                    OptionEntity optionEntity = optionalOption.get();
                    optionEntity.put(optionRequest);
                } else {
                    saveRequestOptionList.add(optionRequest);
                }
            }

            if(!saveRequestOptionList.isEmpty()) {
                List<OptionEntity> optionEntities = OptionEntity.ofList(productEntity, saveRequestOptionList);
                optionJpaRepository.saveAll(optionEntities);
                productEntity.addAllOptionsList(optionEntities);
            }
        }

        return ProductResponse.from(productEntity);
    }

    @Override
    public ProductListResponse getProductListBySeller(Pageable pageable, String search, String status,
                                                      Long categoryId, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        ProductStatus productStatusOfEnums = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).orElse(null);
        Page<ProductResponse> page = productCustomRepositoryImpl.findAll(pageable, search, productStatusOfEnums,
            categoryEntity, memberId);

        return ProductListResponse.from(page);
    }

    @Override
    public CategoryListResponse getCategoryList() {
        List<CategoryEntity> categoryEntityList = categoryJpaRepository.findAll();
        List<CategoryResponse> categoryResponseList = new LinkedList<>();
        for (CategoryEntity c : categoryEntityList) {
            categoryResponseList.add(
                CategoryResponse.builder()
                    .categoryId(c.getId())
                    .name(c.getName())
                    .createdAt(c.getCreatedAt())
                    .modifiedAt(c.getModifiedAt())
                    .build()
            );
        }

        return CategoryListResponse.builder().list(categoryResponseList).build();
    }

    @Transactional
    @Override
    public MessageVo addViewCnt(AddViewCntRequest addViewCntRequest) {
        ProductEntity productEntity = productJpaRepository.findById(addViewCntRequest.getProductId()).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 상품입니다.")
        );
        productEntity.addViewCnt();
        return new MessageVo("조회수 증가 성공");
    }
}
