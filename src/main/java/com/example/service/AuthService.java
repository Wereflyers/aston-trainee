package com.example.service;

import com.example.aop.annotations.Loggable;
import com.example.exceptions.SomeSQLException;
import com.example.exceptions.UserAlreadyRegisteredException;
import com.example.exceptions.WrongDataException;
import com.example.model.User;
import com.example.repository.AuthRepository;
import com.example.repository.AuthRepositoryImpl;

import java.sql.SQLException;

@Loggable
public class AuthService {
    private final AuthRepository authRepository;

    public AuthService() {
        this.authRepository = new AuthRepositoryImpl();
    }

    public String registerUser(User user) {
        try {
            if (authRepository.hasUser(user.getUsername())) {
                throw new UserAlreadyRegisteredException("Такой пользователь уже зарегистрирован");
            }
            return authRepository.registerUser(user.getUsername(), user.getPassword());
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }

    public void authUser(User user) {
        try {
            User savedUser = authRepository.getUser(user.getUsername());
            if (savedUser == null) {
                throw new WrongDataException("Пользователя с таким именем не существует");
            }
            if (!user.getPassword().equals(savedUser.getPassword())) {
                throw new WrongDataException("Введен некорректный пароль");
            }
            System.out.println("Пользователь " + user.getUsername() + " вошел в сеть.");
        } catch (SQLException e) {
            System.out.println("SQLException occurred " + e.getSQLState());
            e.printStackTrace();
        }
    }

    public boolean hasNoUser(String name) {
        try {
            return !authRepository.hasUser(name);
        } catch (SQLException e) {
            return true;
        }
    }
}
