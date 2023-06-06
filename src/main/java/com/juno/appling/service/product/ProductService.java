package com.juno.appling.service.product;

import com.juno.appling.common.s3.S3Service;
import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.product.Product;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.repository.member.MemberRepository;
import com.juno.appling.repository.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ProductVo postProduct(ProductDto productDto, HttpServletRequest request){
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        Product product = productRepository.save(Product.of(member, productDto));
        ProductVo productVo = ProductVo.builder()
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
                .image1(productDto.getImage1())
                .image2(productDto.getImage2())
                .image3(productDto.getImage3())
                .build();

        return productVo;
    }
}
