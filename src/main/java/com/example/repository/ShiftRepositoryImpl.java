package com.example.repository;


import com.example.config.JDBCConfig;
import com.example.model.Shift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftRepositoryImpl implements ShiftRepository {
    private final JDBCConfig config;

    public ShiftRepositoryImpl() {
        this.config = new JDBCConfig();
    }

    @Override
    public Shift getShift(String doctor, LocalDate date) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT * FROM SHIFT S " +
                    "LEFT JOIN DOCTOR D ON D.ID = S.DOCTOR " +
                    "WHERE D.DOCTOR_NAME = ? and S.DATE = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setString(1, doctor);
            preparedStatement.setString(2, date.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                return Shift.builder()
                        .id(id)
                        .date(resultSet.getDate("date").toLocalDate())
                        .doctorName(resultSet.getString("doctor_name"))
                        .offices(getOffices(id))
                        .build();
            }
            return null;
        }
    }

    @Override
    public void remove(long doctorId, LocalDate date) throws SQLException {
        try (Connection connection = config.connect()) {
            String removeDataSql = "DELETE FROM SHIFT " +
                    "WHERE DOCTOR = ? and DATE = ?";
            PreparedStatement statement = connection.prepareStatement(removeDataSql);
            statement.setLong(1, doctorId);
            statement.setString(2, date.toString());
            statement.executeQuery();
        }
    }

    @Override
    public void add(Shift shift, long doctor) throws SQLException {
        try (Connection connection = config.connect()) {
            String insertDataSql = "INSERT INTO shift (DOCTOR, DATE) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
            preparedStatement.setLong(1, doctor);
            preparedStatement.setString(2, shift.getDate().toString());
            preparedStatement.executeUpdate();
        }
        Shift result = getShift(shift.getDoctorName(), shift.getDate());
        addOffices(result.getId(), shift.getOffices());
    }

    @Override
    public boolean hasShift(long doctorId, LocalDate date) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT EXISTS(SELECT * FROM SHIFT WHERE DOCTOR = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setLong(1, doctorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("exists");
            }
            return false;
        }
    }

    private void addOffices(long shiftId, List<Integer> offices) throws SQLException {
        try (Connection connection = config.connect()) {
            for (Integer office : offices) {
                String insertDataSql = "INSERT INTO SHIFT_OFFICE (SHIFT, OFFICE) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
                preparedStatement.setLong(1, shiftId);
                preparedStatement.setInt(2, office);
                preparedStatement.executeUpdate();
            }
        }
    }

    private List<Integer> getOffices(long shiftId) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT * FROM SHIFT_OFFICE " +
                    "WHERE SHIFT = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setLong(1, shiftId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> offices = new ArrayList<>();
            while (resultSet.next()) {
                offices.add(resultSet.getInt("office"));
            }
            return offices;
        }
    }
}
