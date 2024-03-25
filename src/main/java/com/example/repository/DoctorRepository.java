package com.example.repository;

import com.example.model.Doctor;

import java.sql.SQLException;
import java.util.List;

public interface DoctorRepository {

    Doctor getDoctorByName(String name) throws SQLException;

    List<Doctor> getAll() throws SQLException;

    Doctor addDoctor(String name, long specialty) throws SQLException;

    boolean hasDoctorByName(String name) throws SQLException;
}
