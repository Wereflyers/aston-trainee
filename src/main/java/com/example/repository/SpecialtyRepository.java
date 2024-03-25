package com.example.repository;

import java.sql.SQLException;

public interface SpecialtyRepository {

    boolean hasById(long id) throws SQLException;

    String get(long id) throws SQLException;
}