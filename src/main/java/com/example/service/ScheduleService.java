package com.example.service;

import com.example.aop.annotations.Loggable;
import com.example.exceptions.SomeSQLException;
import com.example.exceptions.WrongDataException;
import com.example.model.Doctor;
import com.example.model.Shift;
import com.example.repository.OfficeRepository;
import com.example.repository.OfficeRepositoryImpl;
import com.example.repository.ShiftRepository;
import com.example.repository.ShiftRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Loggable
public class ScheduleService {
    private final ShiftRepository shiftRepository;
    private final DoctorService doctorService;
    private final OfficeRepository officeRepository;

    public ScheduleService() {
        this.officeRepository = new OfficeRepositoryImpl();
        this.shiftRepository = new ShiftRepositoryImpl();
        this.doctorService = new DoctorService();
    }

    public Shift getShift(String doctorName, LocalDate date) {
        try {
            Shift shift = shiftRepository.getShift(doctorName, date);
            if (shift != null) {
                shift.setOffices(officeRepository.getAllForShift(shift.getId()));
            }
            return shift;
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }

    public void removeShift(String doctorName, LocalDate date) {
        Doctor doctor = doctorService.getDoctor(doctorName);
        try {
            if (shiftRepository.hasShift(doctor.getId(), date)) {
                shiftRepository.remove(doctor.getId(), date);
            }
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }

    public Shift addShift(Shift shift) {
        Doctor doctor = doctorService.getDoctor(shift.getDoctorName());
        if (doctor == null) {
            throw new WrongDataException("Некорректное имя врача");
        }
        checkShiftOffices(shift.getOffices());
        try {
            shiftRepository.add(shift, doctor.getId());
            return shiftRepository.getShift(shift.getDoctorName(), shift.getDate());
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }

    private void checkShiftOffices(List<Integer> offices) {
        try {
            for (int office : offices) {
                if (!officeRepository.hasOffice(office)) {
                    throw new WrongDataException("Такого кабинета не существует");
                }
            }
        } catch (SQLException e) {
            throw new SomeSQLException("SQLException occurred " + e.getSQLState());
        }
    }
}
