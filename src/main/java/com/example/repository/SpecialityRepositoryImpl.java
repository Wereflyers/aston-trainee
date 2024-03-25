package com.example.repository;

import com.example.config.JDBCConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialityRepositoryImpl implements SpecialtyRepository {
    private final JDBCConfig config;

    public SpecialityRepositoryImpl() {
        this.config = new JDBCConfig();
    }

    @Override
    public boolean hasById(long id) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT EXISTS(SELECT * FROM medical_speciality WHERE id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1).equals("t");
            }
            return false;
        }
    }

    @Override
    public String get(long id) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT * FROM medical_speciality WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
            return null;
        }
    }
}
