package com.teamside.project.alpha.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.filter.AuthCheck;
import com.teamside.project.alpha.common.handler.AuthenticationHandler;
import com.teamside.project.alpha.common.handler.AuthorizationHandler;
import com.teamside.project.alpha.member.domain.auth.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
public class SpringSecurity {
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final AuthenticationHandler authenticationHandler;
    private final AuthorizationHandler authorizationHandler;

    public SpringSecurity(AuthService authService, ObjectMapper objectMapper, AuthenticationHandler authenticationHandler, AuthorizationHandler authorizationHandler) {
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.authenticationHandler = authenticationHandler;
        this.authorizationHandler = authorizationHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                .csrf().disable()
                .formLogin() .disable()
                .cors().disable()
                .httpBasic().disable()
                        .exceptionHandling()
                        .authenticationEntryPoint(authenticationHandler)
                        .accessDeniedHandler(authorizationHandler)
                        .and()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                        .authorizeRequests()


                        .antMatchers(HttpMethod.GET,"/healthCheck").permitAll()


                        .antMatchers(HttpMethod.POST,"/members/sign-up/**").permitAll()
                        .antMatchers(HttpMethod.GET,"/members/**/exists").permitAll()
                        .antMatchers(HttpMethod.POST, "/members/inquiry").permitAll()
                        .antMatchers(HttpMethod.POST,"/auth/sms/**").permitAll()
                        .antMatchers(HttpMethod.POST,"/auth/refresh/access-token").permitAll()
                        .antMatchers(HttpMethod.POST,"/image/profile/upload").permitAll()





                        .anyRequest().authenticated()
                        .and()
                .addFilterBefore(new AuthCheck(authService, objectMapper), UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}
