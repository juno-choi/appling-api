package com.juno.appling.domain.common.service;

import com.juno.appling.config.s3.S3Service;
import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.domain.common.vo.UploadVo;
import com.juno.appling.domain.member.repository.MemberRepository;
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

    public UploadVo s3UploadFile(List<MultipartFile> files, String pathFormat, HttpServletRequest request){
        Long memberId = tokenProvider.getMemberId(request);

        memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        String s3Url = env.getProperty("cloud.s3.url");

        List<String> fileUrlList = makeFileUrlList(files, memberId,pathFormat);

        return new UploadVo(String.format("%s/%s", s3Url, Optional.ofNullable(fileUrlList.get(0)).orElse("")));
    }

    private List<String> makeFileUrlList(List<MultipartFile> files, Long memberId, String pathFormat) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter pathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String path = now.format(pathFormatter);
        String fileName = now.format(fileNameFormatter);

        return s3Service.putObject(String.format(pathFormat, memberId, path), fileName, files);
    }
}
