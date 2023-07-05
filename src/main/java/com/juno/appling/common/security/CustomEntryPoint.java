package com.juno.appling.common.security;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.juno.appling.domain.dto.ErrorDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomEntryPoint implements AuthenticationEntryPoint {
    @Value("${docs}")
    private String docs;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        List<ErrorDto> errors = new ArrayList<>();
        String requestURI = request.getRequestURI();

        if(requestURI.contains("/login")){
            errors.add(ErrorDto.builder().point("email / password").detail("please check email or password").build());
        }else{
            errors.add(ErrorDto.builder().point("access token").detail("please check access token").build());
        }
        

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.SC_FORBIDDEN), "FORBIDDEN");
        pb.setType(URI.create(docs));
        pb.setProperty("errors", errors);
        pb.setInstance(URI.create(requestURI));
        ObjectMapper objectMapper = new ObjectMapper();

        PrintWriter writer = response.getWriter();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        writer.write(objectMapper.writeValueAsString(pb));
    }
}
