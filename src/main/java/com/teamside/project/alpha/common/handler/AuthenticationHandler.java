package com.teamside.project.alpha.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
public class AuthenticationHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public AuthenticationHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        log.error("[{}] UnAuthorizaed : " + e.getMessage(), httpServletRequest.getRemoteHost() + httpServletRequest.getRequestURI());

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        try (OutputStream os = httpServletResponse.getOutputStream()) {
            objectMapper.writeValue(os, new ResponseObject(ApiExceptionCode.UNAUTHORIZED));
            os.flush();
        }
    }
}
