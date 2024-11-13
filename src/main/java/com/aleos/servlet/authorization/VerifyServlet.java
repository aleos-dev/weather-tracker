package com.aleos.servlet.authorization;

import com.aleos.service.VerificationService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

/**
 * VerifyServlet handles HTTP GET requests for verifying user registration tokens.
 * It extends the AbstractAuthServlet to leverage authentication-related methods and Thymeleaf templates for rendering responses.
 * <p>
 * This servlet provides functionality to:
 * - Validate UUID tokens received as request parameters.
 * - Utilize the VerificationService to check token validity.
 * - Render the appropriate response page based on token verification status.
 */
@WebServlet("/api/v1/verify")
public class VerifyServlet extends AbstractAuthServlet {

    private static final String INVALID_UUID_MESSAGE = "UUID invalid format. It can't be parsed.";
    private static final String VERIFICATION_SUCCESS_MESSAGE = "Verification successful. You can sign in now.";
    private static final String VERIFICATION_ERROR_MESSAGE = "The UUID token %s is invalid.";
    private static final String TOKEN_PARAMETER = "token";

    private transient VerificationService verificationService;

    /**
     * Initializes the VerifyServlet. This method is called by the servlet container to indicate to the servlet
     * that it is being placed into service. It initializes the VerificationService used for validating UUID tokens.
     *
     * @param config the ServletConfig object that contains configuration information for this servlet
     * @throws ServletException if an exception occurs that interferes with the servlet's normal operation
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        verificationService = serviceLocator.getBean(VerificationService.class);
    }

    /**
     * Handles HTTP GET requests for user registration token verification.
     * It validates the UUID token and triggers the verification process or displays an error page if the token is invalid.
     *
     * @param req the HttpServletRequest object that contains the request the client made to the servlet
     * @param res the HttpServletResponse object that contains the response the servlet returns to the client
     */
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
