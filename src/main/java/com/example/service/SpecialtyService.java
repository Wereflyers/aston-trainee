package com.example.service;

import com.example.exceptions.SomeSQLException;
import com.example.repository.SpecialityRepositoryImpl;
import com.example.repository.SpecialtyRepository;

import java.sql.SQLException;

public class SpecialtyService {
    private final SpecialtyRepository specialtyRepository;

    public SpecialtyService() {
        this.specialtyRepository = new SpecialityRepositoryImpl();
    }

    public boolean hasNoSpecialtyById(long id) {
        try {
            return !specialtyRepository.hasById(id);
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }
}
