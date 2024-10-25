package com.aleos.servlet.authorization;

import com.aleos.service.VerificationService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

@WebServlet("/api/v1/verify")
public class VerifyServlet extends AbstractAuthServlet {

    private transient VerificationService verificationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        verificationService = serviceLocator.getBean(VerificationService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        final String cannotParseUuidMessage = "UUID invalid format. It can't be parsed.";

        getUuid(req).ifPresentOrElse(
                uuidToken -> processVerificationResult(uuidToken, req, res),
                () -> renderErrorPage(req, res, cannotParseUuidMessage)
        );
    }

    private void processVerificationResult(UUID uuidToken,
                                           HttpServletRequest req,
                                           HttpServletResponse res) {
        final String verificationErrorMessage = "The UUID token %s is invalid. It can't be verified.";

        if (verificationService.verify(uuidToken)) {
            renderSignInPage(req, res);
        } else {
            renderErrorPage(req, res, verificationErrorMessage.formatted(uuidToken.toString()));
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
}
