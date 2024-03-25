package com.example.in.servlets;

import com.example.aop.annotations.Loggable;
import com.example.dto.UserDto;
import com.example.dto.mapper.UserMapper;
import com.example.exceptions.WrongDataException;
import com.example.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@Loggable
@WebServlet(urlPatterns = "/registration")
public class RegistrationServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        InputStream data = req.getInputStream();
        UserDto userDto = objectMapper.readValue(data, UserDto.class);
        validateUser(userDto);

        String username = authService.registerUser(UserMapper.INSTANCE.userDtoToUser(userDto));
        String result = "Registration is successful. Welcome, " + username;
        resp.setContentType("application/json; charset=UTF-8");
        HttpSession userSession = req.getSession();
        userSession.setAttribute("name", userDto.getUsername());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        PrintWriter out = resp.getWriter();
        out.write(result);
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getUsername().isBlank() || userDto.getUsername().length() < 2
                || userDto.getUsername().length() > 30) {
            throw new WrongDataException("Некорректный логин");
        }
        if (userDto.getPassword().isBlank() || userDto.getPassword().length() < 2
                || userDto.getUsername().length() > 30) {
            throw new WrongDataException("Некорректный пароль");
        }
    }
}
