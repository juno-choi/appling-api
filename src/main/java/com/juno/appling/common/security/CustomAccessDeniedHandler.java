package com.juno.appling.common.security;

import com.juno.appling.domain.dto.ErrorApi;
import com.juno.appling.domain.enums.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        PrintWriter writer = response.getWriter();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        List<String> errorList = new ArrayList<>();
        errorList.add("UNAUTHORIZED");
        ErrorApi<String> errorApi = ErrorApi.<String>builder()
                .code(ResultCode.UNAUTHORIZED.CODE)
                .message(ResultCode.UNAUTHORIZED.MESSAGE)
                .errors(errorList)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(errorApi));
    }
}
