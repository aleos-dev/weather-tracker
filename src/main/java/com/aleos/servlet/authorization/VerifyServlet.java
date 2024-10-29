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

    private static final String INVALID_UUID_MESSAGE = "UUID invalid format. It can't be parsed.";
    private static final String VERIFICATION_SUCCESS_MESSAGE = "Verification successful. You can sign in now.";
    private static final String VERIFICATION_ERROR_MESSAGE = "The UUID token %s is invalid.";
    private static final String TOKEN_PARAMETER = "token";

    private transient VerificationService verificationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        verificationService = serviceLocator.getBean(VerificationService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        getUuid(req)
                .ifPresentOrElse(
                        uuidToken -> processVerificationRequest(uuidToken, req, res),
                        () -> renderErrorPage(req, res, INVALID_UUID_MESSAGE)
                );
    }

    private void processVerificationRequest(UUID uuidToken,
                                            HttpServletRequest req,
                                            HttpServletResponse res) {
        
        if (verificationService.verify(uuidToken)) {

            req.setAttribute(MESSAGE_ATTRIBUTE_KEY, VERIFICATION_SUCCESS_MESSAGE);
            renderSignInPage(req, res);

        } else {
            renderErrorPage(req, res, String.format(VERIFICATION_ERROR_MESSAGE, uuidToken));
        }
    }

    private Optional<UUID> getUuid(HttpServletRequest req) {
        try {
            String token = req.getParameter(TOKEN_PARAMETER);
            UUID tokenUUID = UUID.fromString(token);

            return Optional.of(tokenUUID);

        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
