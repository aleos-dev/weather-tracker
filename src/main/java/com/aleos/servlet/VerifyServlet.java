package com.aleos.servlet;

import com.aleos.service.VerificationService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

@WebServlet("/api/v1/verify")
public class VerifyServlet extends AbstractThymeleafServlet {

    private transient VerificationService verificationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        verificationService = serviceLocator.getBean(VerificationService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        final String cannotParseUuidMessage = "The uuid can not be parsed.";

        getUuid(req).ifPresentOrElse(
                uuidToken -> processVerificationResult(uuidToken, req, res),
                () -> renderErrorPageWithMessage(req, res, cannotParseUuidMessage)
        );
    }

    private void processVerificationResult(UUID uuidToken,
                                           HttpServletRequest req,
                                           HttpServletResponse res) {
        final String verificationErrorMessage = "The token %s cannot be verified.";

        if (verificationService.verify(uuidToken)) {
            renderLoginPage(req, res);
        } else {
            renderErrorPageWithMessage(req, res, verificationErrorMessage.formatted(uuidToken.toString()));
        }
    }

    private Optional<UUID> getUuid(HttpServletRequest req) {
        try {
            String token = req.getParameter("token");
            UUID tokenUUID = UUID.fromString(token);

            return Optional.of(tokenUUID);

        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private void renderLoginPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("login", req, res);
    }
}
