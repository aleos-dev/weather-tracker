package com.aleos.servlet;

import com.aleos.context.listener.TemplateEngineInitializer;
import com.aleos.exception.servlet.RedirectException;
import com.aleos.exception.servlet.ResponseWritingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

public class AbstractThymeleafServlet extends HttpServlet {

    protected transient ITemplateEngine templateEngine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        templateEngine = retrieveTemplateEngine(config);
    }

    protected void processTemplate(String template, HttpServletRequest req, HttpServletResponse res) {
        try {
            var ctx = buildWebContext(req, res);
            templateEngine.process(template, ctx, res.getWriter());
        } catch (IOException e) {
            throw new ResponseWritingException("Failed to write response", e);
        }
    }

    protected void sendRedirect(String redirectUrl, HttpServletResponse res) {
        try {
            res.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new RedirectException("Failed to process redirect to %s".formatted(redirectUrl), e);
        }
    }

    private WebContext buildWebContext(HttpServletRequest req, HttpServletResponse res) {
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(req.getServletContext());

        return new WebContext(application.buildExchange(req, res));
    }

    private ITemplateEngine retrieveTemplateEngine(ServletConfig config) {
        var obj = config.getServletContext()
                .getAttribute(TemplateEngineInitializer.TEMPLATE_ENGINE_CONTEXT_KEY);

        if (obj instanceof ITemplateEngine templateEngineObj) {
            return templateEngineObj;
        } else {
            throw new IllegalStateException("TemplateEngine bean is not of the correct type");
        }
    }

}
