package com.aleos.servlet;

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

@WebServlet("/api/v1/register")
public class RegistrationServlet extends AbstractThymeleafServlet {

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
        renderRegistrationPage(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        if (req.getAttribute("errorData") == null) {
            UserPayload userPayload = parseSimpleDto(UserPayload.class, req);
            var errorDataOpt = validatePayload(validator, userPayload);

            errorDataOpt.ifPresentOrElse(
                    errorData -> req.setAttribute("errorData", errorData),

                    () -> handleRegistration(req, userPayload)
            );
        }

        renderRegistrationPage(req, res);
    }

    private void renderRegistrationPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("register", req, res);
    }

    private void handleRegistration(HttpServletRequest req, UserPayload userPayload) {
        UserVerificationToken token = regService.register(userPayload);
        sendConfirmation(token, req);
        req.setAttribute("successMessage", "Registration successful! Please check your email for verification.");
    }

    private void sendConfirmation(UserVerificationToken token, HttpServletRequest req) {
        String requestUrl = req.getRequestURL().toString().replace("register", "verify");
        String verificationUrl = requestUrl + "?token=" + token.getToken();

        emailService.sendVerificationEmail(token.getUser().getEmail(), verificationUrl);
    }
}
