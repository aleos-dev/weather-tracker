package com.aleos.servlet;

import com.aleos.listener.TemplateEngineInitializer;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/api/v1/login")
public class LoginServlet extends HttpServlet {

    private transient ITemplateEngine templateEngine;

    @Override
    public void init(ServletConfig config) {
        templateEngine = (ITemplateEngine) config.getServletContext()
            .getAttribute(TemplateEngineInitializer.TEMPLATE_ENGINE_CONTEXT_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(req.getServletContext());
        var ctx = new WebContext(application.buildExchange(req, res));

        templateEngine.process("login", ctx, res.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(req.getServletContext());
        var ctx = new WebContext(application.buildExchange(req, res));

        templateEngine.process("hello", ctx, res.getWriter());
    }
}
