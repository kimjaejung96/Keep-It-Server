package com.teamside.project.alpha.common.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class HttpApiWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpRequestServletWrapper httpRequestServletWrapper = new HttpRequestServletWrapper((HttpServletRequest)request);
        HttpResponseServletWrapper httpResponseServletWrapper = new HttpResponseServletWrapper((HttpServletResponse) response);


        chain.doFilter(httpRequestServletWrapper, httpResponseServletWrapper);

        //Response 처리
        String responseMessage = httpResponseServletWrapper.getDataStreamToString();
        responseMessage = deClearXSS(responseMessage);
        byte[] responseMessageBytes = responseMessage.getBytes(StandardCharsets.UTF_8);
        int contentLength = responseMessageBytes.length;

        response.setContentLength(contentLength);
        response.getOutputStream().write(responseMessageBytes);
        response.flushBuffer();
    }

    @Override
    public void destroy() {
        // Do nothing
    }
    private String deClearXSS(String value) {
        value = value.replaceAll("& lt;","<").replaceAll( "& gt;",">");
        value = value.replaceAll( "& #40;", "\\(").replaceAll( "& #41;", "\\)");
        value = value.replaceAll( "& #39;", "'");
        //        value = value.replaceAll( "\"\"", "[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']");
        //        value = value.replaceAll( "", "script");

        return value;
    }

}
