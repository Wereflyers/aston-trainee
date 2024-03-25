package com.example.repository;


import com.example.model.Shift;

import java.sql.SQLException;
import java.time.LocalDate;

public interface ShiftRepository {

    Shift getShift(String doctor, LocalDate date) throws SQLException;

    void remove(long doctorId, LocalDate date) throws SQLException;

    void add(Shift shift, long doctor) throws SQLException;

    boolean hasShift(long doctorId, LocalDate date) throws SQLException;
}
