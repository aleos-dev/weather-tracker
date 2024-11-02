package com.aleos.servlet.authorization;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.model.entity.User;
import com.aleos.service.VerificationService;
import com.aleos.servlet.AbstractTestServlet;
import com.aleos.util.DbUtil;
import com.aleos.util.ReflectionUtil;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyServletTest extends AbstractTestServlet {

    private static VerifyServlet realServlet;
    private VerifyServlet spyServlet = new VerifyServlet();

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
    void doGet_ShouldRenderSignInAndVerifyUser_WhenTokenIsValid() {
        var username = "verify_1";
        setupAndMockNonVerifiedUser(username, "testPassword", "verify_1@localhost.com");

        spyServlet.doGet(request, response);

        verify(request).setAttribute(eq("message"), anyString());
        verifyTemplateRendered("sign-in", spyServlet);
        assertUserIsVerified(username);
    }

    @ParameterizedTest
    @CsvSource({
            "invalid_token",
            "30fa3bea-7b5f-4b79-9882-c314e8416cde"
    })
    void doGet_ShouldRenderErrorPage_WhenTokenIsInvalidOrAbsent(String verificationToken) {
        mockRequestParameters(verificationToken);

        spyServlet.doGet(request, response);

        verify(request, never()).setAttribute(eq("message"), anyString());
        verify(request).setAttribute(eq("errors"), any());
        verifyTemplateRendered("errorPage", spyServlet);
    }

    private static void initializeDependencies() {
        realServlet = new VerifyServlet();
        ReflectionUtil.setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
        ReflectionUtil.setFieldToObject(realServlet, "verificationService", TestContextInitializer.getBean(VerificationService.class));
    }

    private void setupAndMockNonVerifiedUser(String username, String password, String email) {
        String verificationToken = setupNoVerifiedUser(username, password, email).getToken().toString();
        mockRequestParameters(verificationToken);
    }

    private void mockRequestParameters(String token) {
        doReturn(token).when(request).getParameter("token");
    }

    private void assertUserIsVerified(String username) {
        Optional<User> user = DbUtil.findUser(username);

        assertTrue(user.isPresent(), "Expected user to be present but was not found");
        assertTrue(user.get().isVerified(), "Expected user to be verified but was not.");
    }
}