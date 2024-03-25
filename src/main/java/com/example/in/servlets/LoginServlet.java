package com.example.in.servlets;


import com.example.aop.annotations.Loggable;
import com.example.dto.UserDto;
import com.example.dto.mapper.UserMapper;
import com.example.exceptions.NoRightsException;
import com.example.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@Loggable
@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) {
        this.authService = new AuthService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        InputStream data = request.getInputStream();
        UserDto userDto = objectMapper.readValue(data, UserDto.class);

        if (userDto.getUsername() != null && userDto.getPassword() != null) {
            authService.authUser(UserMapper.INSTANCE.userDtoToUser(userDto));
            HttpSession userSession = request.getSession();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json; charset=UTF-8");
            userSession.setAttribute("name", userDto.getUsername());
            String result = "Login successful. Welcome, " + userDto.getUsername();
            response.setContentType("application/json; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(result);
        } else {
            throw new NoRightsException("Неверный логин / пароль");
        }
    }
}
