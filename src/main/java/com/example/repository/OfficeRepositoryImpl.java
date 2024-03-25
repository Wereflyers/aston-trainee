package com.example.repository;

import com.example.config.JDBCConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfficeRepositoryImpl implements OfficeRepository {
    private final JDBCConfig config;

    public OfficeRepositoryImpl() {
        this.config = new JDBCConfig();
    }

    @Override
    public boolean hasOffice(int office) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT EXISTS(SELECT * FROM office WHERE OFFICE_NUMBER = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setInt(1, office);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1).equals("t");
            }
            return false;
        }
    }

    @Override
    public List<Integer> getAllForShift(long id) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT * FROM SHIFT_OFFICE SO " +
                    "LEFT JOIN OFFICE O ON SO.OFFICE = O.ID " +
                    "WHERE SO.SHIFT = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> offices = new ArrayList<>();
            while (resultSet.next()) {
                offices.add(resultSet.getInt("O.OFFICE_NUMBER"));
            }
            return offices;
        }
    }
}
