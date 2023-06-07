package com.juno.appling.service.common;

import com.juno.appling.common.s3.S3Service;
import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.vo.common.UploadVo;
import com.juno.appling.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonS3Service {
    private final TokenProvider tokenProvider;
    private final S3Service s3Service;
    private final Environment env;
    private final MemberRepository memberRepository;

    public UploadVo uploadImage(List<MultipartFile> files, HttpServletRequest request){
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);

        memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        String s3Url = env.getProperty("cloud.s3.url");

        List<String> fileUrlList = getFileUrlList(files, memberId);

        return UploadVo.builder()
                .imageUrl(String.format("%s/%s", s3Url, Optional.ofNullable(fileUrlList.get(0)).orElse("")))
                .build();
    }

    private List<String> getFileUrlList(List<MultipartFile> files, Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter pathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String path = now.format(pathFormatter);
        String fileName = now.format(fileNameFormatter);

        List<String> putFileUrlList = s3Service.putObject(String.format("image/%s/%s/", memberId, path), fileName, files);
        return putFileUrlList;
    }
}
