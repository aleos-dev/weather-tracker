package com.aleos.servlet.authorization;

import com.aleos.model.ErrorDetails;
import com.aleos.model.UserPayload;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.service.EmailService;
import com.aleos.service.RegistrationService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;

import java.util.Optional;

/**
 * The SignUpServlet class handles user registration by processing HTTP GET and POST requests.
 * It extends the AbstractAuthServlet to utilize its authentication-related functionalities.
 * <p>
 * The servlet initializes necessary services for user registration, including validation,
 * registration handling, and email communication.
 * <p>
 * On HTTP GET requests, it displays the sign-up page.
 * <p>
 * On HTTP POST requests, it validates the user-provided registration details. If the validation
 * is successful, it proceeds to register the user and sends a verification email. If validation fails,
 * it re-renders the sign-up page with the corresponding error messages.
 */
@WebServlet("/api/v1/sign-up")
public class SignUpServlet extends AbstractAuthServlet {

    private static final String REGISTRATION_SUCCESS_MESSAGE = "To complete registration, please check your email for" +
                                                               " the verification.";
    private transient Validator validator;
    private transient RegistrationService registrationService;
    private transient EmailService emailService;

    /**
     * Initializes the SignUpServlet by configuring initial state and services.
     *
     * @param config the ServletConfig that provides configuration information for this servlet
     * @throws ServletException if an exception occurs that interrupts the servlet's normal operation
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        initializeServices();
    }

    /**
     * Handles HTTP GET requests. This method is invoked when a user sends a GET request
     * to the servlet. It renders the sign-up page.
     *
     * @param req  the HttpServletRequest object that contains the request the client
     *             has made to the servlet.
     * @param res  the HttpServletResponse object that contains the response the
     *             servlet sends to the client.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        renderSignUpPage(req, res);
    }

    /**
     * Handles the POST request for user registration.
     * This method processes the user sign-up by validating the input payload,
     * updating request attributes, and rendering appropriate views based on
     * validation results.
     *
     * @param req the HttpServletRequest containing the user input data
     * @param res the HttpServletResponse used to send responses
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        if (hasNoErrorAttribute(req)) {
            UserPayload inputPayload = parseSimpleDto(UserPayload.class, req);
            updateAttributesForTemplate(req, inputPayload);

            Optional<ErrorDetails> validationError = validatePayload(validator, inputPayload);
            if (validationError.isEmpty()) {
                processUserSignUp(inputPayload, req, res);
                return;
            }
            req.setAttribute(ERROR_ATTRIBUTE_KEY, validationError.get());
        }

        renderSignUpPage(req, res);
    }

    private void processUserSignUp(UserPayload userPayload, HttpServletRequest req, HttpServletResponse res) {
        UserVerificationToken token = registrationService.register(userPayload);
        sendConfirmation(token, req);
        req.setAttribute(MESSAGE_ATTRIBUTE_KEY, REGISTRATION_SUCCESS_MESSAGE);

        renderSignInPage(req, res);
    }

    private void sendConfirmation(UserVerificationToken token, HttpServletRequest req) {
        String requestUrl = req.getRequestURL().toString().replace("sign-up", "verify");
        String verificationUrl = requestUrl + "?token=" + token.getToken();

        emailService.sendVerificationEmail(token.getUser().getEmail(), verificationUrl);
    }

    private void updateAttributesForTemplate(HttpServletRequest req, UserPayload inputPayload) {
        req.setAttribute("name", inputPayload.getUsername());
        req.setAttribute("email", inputPayload.getEmail());
    }

    private void initializeServices() {
        this.validator = serviceLocator.getBean(Validator.class);
        this.registrationService = serviceLocator.getBean(RegistrationService.class);
        this.emailService = serviceLocator.getBean(EmailService.class);
    }
}
