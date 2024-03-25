package com.example.repository;

import java.sql.SQLException;
import java.util.List;

public interface OfficeRepository {
    boolean hasOffice(int office) throws SQLException;

    List<Integer> getAllForShift(long id) throws SQLException;
}
