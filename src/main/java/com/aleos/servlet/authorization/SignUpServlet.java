package com.aleos.servlet.authorization;

import com.aleos.model.ErrorData;
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

@WebServlet("/api/v1/sign-up")
public class SignUpServlet extends AbstractAuthServlet {

    private transient Validator validator;
    private transient RegistrationService regService;
    private transient EmailService emailService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.validator = serviceLocator.getBean(Validator.class);
        this.regService = serviceLocator.getBean(RegistrationService.class);
        this.emailService = serviceLocator.getBean(EmailService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        renderSignUpPage(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        if (hasNoErrorAttribute(req)) {
            UserPayload inputPayload = parseSimpleDto(UserPayload.class, req);
            Optional<ErrorData> validationError = validatePayload(validator, inputPayload);

            if (validationError.isEmpty()) {
                processUserSignUp(inputPayload, req, res);
                return;
            }
            req.setAttribute(ERROR_ATTRIBUTE_KEY, validationError.get());
        }
        renderSignUpPage(req, res);
    }

    private void processUserSignUp(UserPayload userPayload, HttpServletRequest req, HttpServletResponse res) {
        UserVerificationToken token = regService.register(userPayload);
        sendConfirmation(token, req);
        req.setAttribute(MESSAGE_ATTRIBUTE_KEY, "Registration successful! Please check your email for verification.");
        renderSignInPage(req, res);
    }


    private void sendConfirmation(UserVerificationToken token, HttpServletRequest req) {
        String requestUrl = req.getRequestURL().toString().replace("register", "verify");
        String verificationUrl = requestUrl + "?token=" + token.getToken();

        emailService.sendVerificationEmail(token.getUser().getEmail(), verificationUrl);
    }
}
