package com.github.printparam;

import com.github.printparam.starter.PrintParamProperties;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author qinkangdeid
 */
@Slf4j(topic = "print-param:")
public class ParamFilter implements Filter {

    private PrintParamProperties properties;

    public ParamFilter(PrintParamProperties properties) {
        this.properties = properties;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (properties == null) {
            properties = new PrintParamProperties();
        }
        HttpServletRequest r = (HttpServletRequest) servletRequest;

        if (isRequestExcluded(r)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            ParamRequestWrapper requestWrapper = new ParamRequestWrapper((HttpServletRequest) servletRequest);

            if (properties.isEnableInputParam()) {
                String path = r.getQueryString();
                if (path == null) {
                    Map<String, String> map = new HashMap<>();
                    Enumeration headerNames = ((HttpServletRequest) servletRequest).getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        String key = (String) headerNames.nextElement();
                        String value = ((HttpServletRequest) servletRequest).getHeader(key);
                        map.put(key, value);
                    }
                    path = map.toString();
                }
                String url = r.getRequestURI();
                log.info("[request uri]:{}", url);
                if (properties.isEnableHeaderParam()) {
                    log.info("[request header]:{}", path);
                }
                try {
                    Map<String, String[]> map = servletRequest.getParameterMap();
                    log.info("[request form]:");
                    map.forEach((k, v) -> log.info("{}:{}", k, v));
                    BufferedReader bufferedReader = requestWrapper.getReader();
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    if (sb.length() > 0) {
                        log.info("[request body]:" + sb.toString());
                    }
                } catch (Exception e) {
                    log.warn("[request error]:", e);
                }
            }
            ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
            filterChain.doFilter(requestWrapper, responseWrapper);

            String result = new String(responseWrapper.getResponseData());
            servletResponse.setContentLength(-1);
            servletResponse.setCharacterEncoding("UTF-8");
            PrintWriter out = null;
            try {
                out = servletResponse.getWriter();
                out.write(result);
                out.flush();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            if (properties.isEnableOutputResult()) {
                log.info("[response return data]:" + result);
            }
        }
    }


    @Override
    public void destroy() {
    }

    private boolean isRequestExcluded(HttpServletRequest httpRequest) {
        return this.properties.getFilterExcludePattern() != null
                && Pattern.compile(this.properties.getFilterExcludePattern())
                .matcher(httpRequest.getRequestURI().substring(httpRequest.getContextPath().length()))
                .matches();
    }
}
