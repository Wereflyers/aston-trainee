package com.example.repository;

import com.example.config.JDBCConfig;
import com.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthRepositoryImpl implements AuthRepository {
    private final JDBCConfig config;

    public AuthRepositoryImpl() {
        this.config = new JDBCConfig();
    }

    @Override
    public String registerUser(String username, String password) throws SQLException {
        try (Connection connection = config.connect()) {
            String insertDataSql = "INSERT INTO users_table (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            return username;
        }
    }

    @Override
    public boolean hasUser(String username) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT " +
                    "EXISTS(SELECT * FROM users_table WHERE username = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1).equals("t");
            }
            return false;
        }
    }

    @Override
    public User getUser(String username) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT * FROM users_table WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                String savedName = resultSet.getString("username");
                String savedPassword = resultSet.getString("password");
                user = new User(savedName, savedPassword);
            }
            return user;
        }
    }
}
