package com.teamside.project.alpha.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.domain.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AuthCheck extends OncePerRequestFilter {
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    public AuthCheck(AuthService authService, ObjectMapper objectMapper, Environment environment) {
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String activeYml = environment.getActiveProfiles()[0];
        try {
            String token = getAccessToken(request);
            if (!activeYml.equals("prd")) {
                log.info("\nAccess Token : {}", token);
            }
            if(token != null){
                authService.tokenValidationCheck(token);
                String mid = authService.getAuthPayload(getAccessToken(request));
                Authentication authentication = new UsernamePasswordAuthenticationToken(mid, getAccessToken(request),null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (CustomException | CustomRuntimeException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), new ResponseObject(ApiExceptionCode.UNAUTHORIZED));
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(KeepitConstant.ACCESS_TOKEN);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
