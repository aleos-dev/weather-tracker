package com.aleos.security.web.filters;

import com.aleos.context.Properties;
import com.aleos.exception.context.AuthenticationException;
import com.aleos.http.CustomHttpSession;
import com.aleos.http.CustomHttpSessionImpl;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.AuthenticationToken;
import com.aleos.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    private static final String USERNAME_PARAM = "name";
    private static final String PASSWORD_PARAM = "password";
    private static final String AUTH_METHOD = "POST";
    private static final String AUTH_URI = Properties.get("auth.url").orElse("/api/v1/sign-in");

    private AuthenticationFilter authenticationFilter;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private CustomHttpSessionImpl session;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authenticationFilter = spy(new AuthenticationFilter(authenticationService));
    }

    @ParameterizedTest
    @MethodSource("absentAuthenticationData")
    void shouldProceedWithOriginalChainWhenUnauthenticatedAndInvalidRequest(Authentication authentication,
                                                                            String method, String uri) throws ServletException, IOException {
        mockRequestAttributes(authentication, method, uri);

        authenticationFilter.doFilter(request, response, chain);

        verify(session).getAuthentication();
        verify(request, never()).getParameter(USERNAME_PARAM);
        verify(request, never()).getParameter(PASSWORD_PARAM);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateWhenNoSessionAuthentication() throws ServletException, IOException {
        var username = "auth_user_1";
        var password = "auth_password_1";

        mockRequestAttributes(null, AUTH_METHOD, AUTH_URI);
        mockUserCredentials(username, password);

        when(authenticationService.authenticate(username, password)).thenReturn(authentication);
        doNothing().when(session).setAuthentication(authentication);

        authenticationFilter.doFilter(request, response, chain);

        verify(session).getAuthentication();
        verify(request).getParameter(USERNAME_PARAM);
        verify(request).getParameter(PASSWORD_PARAM);
        verify(authenticationService).authenticate(username, password);
        verify(session).setAuthentication(authentication);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldHandleAuthenticationExceptionAndSetOriginalRequest() throws ServletException, IOException {
        var username = "auth_user_1";
        var password = "auth_password_1";

        mockRequestAttributes(null, AUTH_METHOD, AUTH_URI);
        mockUserCredentials(username, password);

        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher(AUTH_URI)).thenReturn(dispatcher);
        doThrow(AuthenticationException.class).when(authenticationService).authenticate(username, password);

        authenticationFilter.doFilter(request, response, chain);

        verify(session).getAuthentication();
        verify(request).getParameter(USERNAME_PARAM);
        verify(request).getParameter(PASSWORD_PARAM);
        verify(authenticationService).authenticate(username, password);
        verify(session).setOriginalRequest(AUTH_URI);
        verify(request).setAttribute(eq("errors"), any());
        verify(dispatcher).forward(request, response);
    }

    private void mockRequestAttributes(Authentication authentication, String method, String uri) {
        when(request.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY)).thenReturn(session);
        when(session.getAuthentication()).thenReturn(Optional.ofNullable(authentication));
        when(request.getMethod()).thenReturn(method);
        lenient().when(request.getRequestURI()).thenReturn(uri);
    }

    private void mockUserCredentials(String username, String password) {
        when(request.getParameter(USERNAME_PARAM)).thenReturn(username);
        when(request.getParameter(PASSWORD_PARAM)).thenReturn(password);
    }

    private static Stream<Arguments> absentAuthenticationData() {
        AuthenticationToken notAuthenticatedToken = mock(AuthenticationToken.class);
        when(notAuthenticatedToken.setAuthenticated()).thenReturn(false);

        AuthenticationToken anonymousToken = mock(AuthenticationToken.class);
        when(anonymousToken.isAnonymous()).thenReturn(true);

        return Stream.of(
                Arguments.of(null, "GET", AUTH_URI),
                Arguments.of(null, AUTH_METHOD, "NO" + AUTH_URI),
                Arguments.of(null, "GET", "NO" + AUTH_URI),
                Arguments.of(notAuthenticatedToken, "GET", AUTH_URI),
                Arguments.of(notAuthenticatedToken, AUTH_METHOD, "NO" + AUTH_URI),
                Arguments.of(notAuthenticatedToken, "GET", "NO" + AUTH_URI),
                Arguments.of(anonymousToken, "GET", AUTH_URI),
                Arguments.of(anonymousToken, AUTH_METHOD, "NO" + AUTH_URI),
                Arguments.of(anonymousToken, "GET", "NO" + AUTH_URI)
        );
    }
}
