package com.aleos.servlet.authorization;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.exception.repository.UniqueConstraintViolationException;
import com.aleos.model.entity.User;
import com.aleos.repository.UserRepository;
import com.aleos.service.EmailService;
import com.aleos.service.RegistrationService;
import com.aleos.servlet.AbstractTestServlet;
import jakarta.validation.Validator;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.aleos.util.ReflectionUtil.setFieldToObject;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServletTest extends AbstractTestServlet {

    private static SignUpServlet realServlet;
    private static SignUpServlet spyServlet;

    @Mock
    private EmailService emailService;

    @BeforeAll
    static void beforeAll() {
        initializeDependencies();
        TestContextInitializer.getBean(Flyway.class).migrate();
    }

    @AfterAll
    static void afterAll() {
        TestContextInitializer.getBean(Flyway.class).clean();
    }

    @BeforeEach
    void setUp() {
        spyServlet = Mockito.spy(realServlet);
        configureSpyServlet(spyServlet);
    }

    @Test
    void doGet_ShouldRenderSignUpPage() {
        spyServlet.doGet(request, response);

        verifyTemplateRendered("sign-up", spyServlet);
    }

    @Test
    void doPost_ShouldRenderSignUpPage_WhenErrorsArePresent() {
        mockRequestParameter("errors", "SOME_ERROR");

        spyServlet.doPost(request, response);

        verify(request).getAttribute("errors");
        verify(request, never()).setAttribute(eq("name"), anyString());
        verify(request, never()).setAttribute(eq("email"), anyString());
        verifyTemplateRendered("sign-up", spyServlet);
    }

    @Test
    void doPost_ShouldRenderSignUpPage_WhenDataIsCorrect() {
        setupSignUpRequest("sign_up_user_1", "password1", "sign_up_email_1@localhost.com");

        spyServlet.doPost(request, response);

        verify(request).setAttribute(eq("name"), anyString());
        verify(request).setAttribute(eq("email"), anyString());
        verify(emailService).sendVerificationEmail(eq("sign_up_email_1@localhost.com"), anyString());
        verify(request).setAttribute(eq("message"), anyString());
        verifyTemplateRendered("sign-in", spyServlet);
        assertUserNotVerified("sign_up_user_1");

    }

    @ParameterizedTest
    @MethodSource("invalidSignUpData")
    void doPost_ShouldRenderSignUpPageWithErrors_WhenDataIsInvalid(String username, String password, String email) {
        mockUserPayloadRequest(username, password, email);

        spyServlet.doPost(request, response);

        verify(request).setAttribute(eq("name"), any());
        verify(request).setAttribute(eq("email"), any());
        verify(request).getAttribute("errors");
        verifyTemplateRendered("sign-up", spyServlet);
    }

    @Test
    void doPost_ShouldThrowUniqueConstraintViolationException_WhenUsernameAlreadyExists() {
        var username = "test_user_uniqueness";
        setupNoVerifiedUser(username, "any-pass_1", "anyemail_1@localhost.com");
        mockUserPayloadRequest(username, "any-pass_2", "anyemail_2@localhost.com");

        assertThrows(UniqueConstraintViolationException.class, () -> spyServlet.doPost(request, response));
    }

    @Test
    void doPost_ShouldThrowUniqueConstraintViolationException_WhenEmailAlreadyExists() {
        var email = "test_email_uniqueness@localhost.com";
        setupNoVerifiedUser("any-user_1", "any-pass_1", email);
        mockUserPayloadRequest("any-user_2", "any-pass_2", email);

        assertThrows(UniqueConstraintViolationException.class, () -> spyServlet.doPost(request, response));
    }

    private static void initializeDependencies() {
        realServlet = new SignUpServlet();
        setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
        setFieldToObject(realServlet, "validator", TestContextInitializer.getBean(Validator.class));
        setFieldToObject(realServlet, "registrationService", TestContextInitializer.getBean(RegistrationService.class));
    }

    private static Stream<Arguments> invalidSignUpData() {
        return Stream.of(
                Arguments.of("ab", "123", "sign_up_email_2@localhost.com"),       // Invalid username (too short)
                Arguments.of("", "123", "sign_up_email_2@localhost.com"),         // Empty username
                Arguments.of(null, "123", "sign_up_email_2@localhost.com"),       // Null username
                Arguments.of("good_user", "12", "sign_up_email_2@localhost.com"), // Invalid password (too short)
                Arguments.of("good_user", "", "sign_up_email_2@localhost.com"),   // Empty password
                Arguments.of("good_user", null, "sign_up_email_2@localhost.com"), // Null password
                Arguments.of("good_user", "good_pass", "sign_up_email_2"),        // Invalid email (missing domain)
                Arguments.of("good_user", "good_pass", "@localhost.com"),         // Invalid email (missing username)
                Arguments.of("good_user", "good_pass", null)                      // Null email
        );
    }

    private void setupSignUpRequest(String username, String password, String email) {
        setFieldToObject(spyServlet, "emailService", emailService);
        mockUserPayloadRequest(username, password, email);
        doReturn(new StringBuffer("https://")).when(request).getRequestURL();
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());
    }

    private void mockRequestParameter(String key, String value) {
        doReturn(value).when(request).getAttribute(key);
    }

    private void mockUserPayloadRequest(String username, String password, String email) {
        doReturn(username).when(request).getParameter("name");
        doReturn(password).when(request).getParameter("password");
        doReturn(email).when(request).getParameter("email");
    }

    private void assertUserNotVerified(String username) {
        assertFalse(
                TestContextInitializer.getBean(UserRepository.class).find(username).map(User::isVerified).orElse(true),
                "User should not be verified at this stage"
        );
    }
}