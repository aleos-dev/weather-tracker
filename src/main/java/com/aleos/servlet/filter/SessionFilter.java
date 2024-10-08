package com.aleos.servlet.filter;

import com.aleos.http.CustomHttpSession;
import com.aleos.http.SessionManager;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SessionFilter extends HttpFilter {

    private transient SessionManager manager;

    @Override
    public void init(FilterConfig config) {
        var locator = (ServiceLocator) config.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        manager = (SessionManager) locator.getBean(CustomHttpSession.class);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        CustomHttpSession session = manager.getValidSession(req, res)
                .orElse(manager.createSession(res));

        req.setAttribute(CustomHttpSession.SESSION_CONTEXT_KEY, session);

        chain.doFilter(req, res);
    }
}
