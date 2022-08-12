package com.teamside.project.alpha.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.member.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthCheck extends OncePerRequestFilter {
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public AuthCheck(AuthService authService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

//    private static final String[] EXCLUDE_URL = {
//            "/members/**/exists",
//            "/members/sign-up",
//            "/ping",
//    };
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        AntPathMatcher pathMatcher = new AntPathMatcher();
//        String url = request.getRequestURI();
//        return Stream.of(EXCLUDE_URL).anyMatch(x -> pathMatcher.match(x, url));
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getAccessToken(request);
            if(token != null){
                authService.tokenValidationCheck(token);
                String mid = authService.getAuthPayload(getAccessToken(request));
                Authentication authentication = new UsernamePasswordAuthenticationToken(mid, getAccessToken(request),null);;
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
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
