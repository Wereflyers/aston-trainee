package com.example.service;

import com.example.aop.annotations.Loggable;
import com.example.exceptions.SomeSQLException;
import com.example.exceptions.WrongDataException;
import com.example.model.Doctor;
import com.example.repository.DoctorRepository;
import com.example.repository.DoctorRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

@Loggable
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final SpecialtyService specialtyService;

    public DoctorService() {
        this.doctorRepository = new DoctorRepositoryImpl();
        this.specialtyService = new SpecialtyService();
    }

    public Doctor getDoctor(String name) {
        try {
            return doctorRepository.getDoctorByName(name);
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }

    public List<Doctor> getAll() {
        try {
            return doctorRepository.getAll();
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }

    public Doctor addDoctor(String name, long specialty) {
        try {
            if (specialtyService.hasNoSpecialtyById(specialty)) {
                throw new WrongDataException("Специальность не существует");
            }
            return doctorRepository.addDoctor(name, specialty);
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }

    public boolean hasNoDoctorByName(String name) {
        try {
            return !doctorRepository.hasDoctorByName(name);
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }
}
