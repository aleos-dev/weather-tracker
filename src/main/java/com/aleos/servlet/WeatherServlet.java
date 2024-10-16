package com.aleos.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/weather")
public class WeatherServlet extends AbstractThymeleafServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("weather", req, res);
    }
}
