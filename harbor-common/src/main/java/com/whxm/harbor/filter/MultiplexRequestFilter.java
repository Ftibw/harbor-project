package com.whxm.harbor.filter;

import com.whxm.harbor.bean.BodyReaderRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MultiplexRequestFilter implements Filter {

    @Override  
    public void destroy() {  
  
    }  
  
    @Override  
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        ServletRequest requestWrapper = null;

        if (request instanceof HttpServletRequest) {

            HttpServletRequest req = (HttpServletRequest) request;

            if ("POST".equals(req.getMethod().toUpperCase())
                    && req.getContentType()
                    .equalsIgnoreCase("application/json")) {

                requestWrapper = new BodyReaderRequestWrapper((HttpServletRequest) request);
            }
        }
  
        if (requestWrapper == null) {  
            chain.doFilter(request, response);  
        } else {  
            chain.doFilter(requestWrapper, response);   
        }  
    }  
  
    @Override
    public void init(FilterConfig config) throws ServletException {

    }
  
}  