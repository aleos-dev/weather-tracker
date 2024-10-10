package com.aleos.servlet;

import com.aleos.context.Properties;
import com.aleos.model.UserPayload;
import com.aleos.model.entity.UserVerification;
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
        req.setAttribute("appContext", Properties.get("app.context").orElse("/"));
        processRegistration(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        UserPayload userPayload = parseSimpleDto(UserPayload.class, req);
        var constraintViolations = validatePayload(validator, userPayload);

        constraintViolations.ifPresentOrElse(
                violations -> {
                    req.setAttribute("errors", violations);
                    processRegistration(req, res);
                },
                () -> {
                    var tokenOpt = regService.register(userPayload);
                    tokenOpt.ifPresentOrElse(token -> sendConfirmation(token, req),
                            () -> {
                                req.setAttribute("errorMessage", "User already exists");
                                processRegistration(req, res);
                            }
                    );
                }
        );
    }

    private void sendConfirmation(UserVerification token, HttpServletRequest req) {
        String requestUrl = req.getRequestURL().toString().replace("register", "verify");
        String verificationUrl = requestUrl + "?token=" + token.getToken();

        emailService.sendVerificationEmail(token.getUser().getEmail(), verificationUrl);
    }

    private void processRegistration(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("registration", req, res);
    }

}
