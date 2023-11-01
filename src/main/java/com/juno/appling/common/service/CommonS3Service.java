package com.juno.appling.common.service;

import com.juno.appling.common.controller.response.UploadResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface CommonS3Service {
    UploadResponse s3UploadFile(List<MultipartFile> files, String pathFormat,
        HttpServletRequest request);
}
