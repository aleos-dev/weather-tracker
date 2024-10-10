package com.aleos.servlet;

import com.aleos.context.Properties;
import com.aleos.model.entity.User;
import com.aleos.service.VerificationService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

@WebServlet("/api/v1/verify")
public class VerifyServlet extends AbstractThymeleafServlet {

    private static final String ERROR_PAGE_URL = Properties.get("error.url").orElse("/");

    private transient VerificationService verificationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {

        getUuid(req).ifPresentOrElse(
                token -> {
                    Optional<User> verify = verificationService.verify(token);
                    verify.ifPresent(user -> processTemplate("login", req, res));
                },
                () -> {
                    req.setAttribute("errorMessage", "The uuid not valid.");
                    sendRedirect(ERROR_PAGE_URL, res);
                });
    }

    private static Optional<UUID> getUuid(HttpServletRequest req) {
        try {
            String token = req.getParameter("token");
            UUID tokenUUID = UUID.fromString(token);

            return Optional.of(tokenUUID);

        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
