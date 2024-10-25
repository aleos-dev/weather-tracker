package com.aleos.servlet.authorization;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/sign-in")
public class SignInServlet extends AbstractAuthServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        renderSignInPage(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        renderSignInPage(req, res);
    }
}
