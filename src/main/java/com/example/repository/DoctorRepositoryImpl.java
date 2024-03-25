package com.example.repository;

import com.example.config.JDBCConfig;
import com.example.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepositoryImpl implements DoctorRepository {
    private final JDBCConfig config;

    public DoctorRepositoryImpl() {
        this.config = new JDBCConfig();
    }

    @Override
    public Doctor getDoctorByName(String name) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT * FROM doctor D " +
                    "LEFT JOIN medical_speciality SPEC ON D.SPECIALTY = SPEC.id " +
                    "WHERE D.doctor_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getDoctorFromRow(resultSet);
            }
            return null;
        }
    }

    @Override
    public boolean hasDoctorByName(String name) throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT EXISTS(SELECT * FROM doctor WHERE doctor_name = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(checkDataSql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("exists");
            }
            return false;
        }
    }

    @Override
    public List<Doctor> getAll() throws SQLException {
        try (Connection connection = config.connect()) {
            String checkDataSql = "SELECT * FROM DOCTOR D " +
                    "LEFT JOIN medical_speciality SPEC ON D.SPECIALTY = SPEC.id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(checkDataSql);
            List<Doctor> indicationTypeList = new ArrayList<>();
            while (resultSet.next()) {
                indicationTypeList.add(getDoctorFromRow(resultSet));
            }
            return indicationTypeList;
        }
    }

    @Override
    public Doctor addDoctor(String name, long specialty) throws SQLException {
        try (Connection connection = config.connect()) {
            String insertDataSql = "INSERT INTO doctor (doctor_name, SPECIALTY) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, specialty);
            preparedStatement.executeUpdate();
        }
        return getDoctorByName(name);
    }

    private static Doctor getDoctorFromRow(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String doctorName = resultSet.getString("doctor_name");
        String speciality = resultSet.getString("name");
        return new Doctor(id, doctorName, speciality);
    }
}