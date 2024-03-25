package com.example.in.servlets;

import com.example.aop.annotations.Loggable;
import com.example.exceptions.NoRightsException;
import com.example.exceptions.WrongDataException;
import com.example.service.DoctorService;
import com.example.service.ScheduleService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@Loggable
@WebServlet(urlPatterns = "/shift/remove")
public class RemoveShiftServlet extends HttpServlet {
    private ScheduleService scheduleService;
    private DoctorService doctorService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.scheduleService = new ScheduleService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String username;
        try {
            username = req.getHeader("username");
        } catch (NullPointerException e) {
            throw new WrongDataException("Введите имя пользователя");
        }
        if (!username.equals("admin")) {
            throw new NoRightsException("Вы имеете недостаточно прав для выполнения данной операции");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(req.getParameter("date"));
        } catch (NullPointerException e) {
            throw new WrongDataException("Введите корректную дату в формате yyyy-MM-dd");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new WrongDataException("Невозможно удалить смену за прошедший день");
        }
        String doctor = getDoctor(req, username);

        scheduleService.removeShift(doctor, date);
        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private String getDoctor(HttpServletRequest req, String username) {
        String doctor;
        try {
            doctor = req.getParameter("doctor");
        } catch (NullPointerException e) {
            doctor = username;
        }
        if (doctor == null || doctor.isBlank() || doctorService.hasNoDoctorByName(doctor)) {
            throw new WrongDataException("Некорректное имя врача");
        }
        return doctor;
    }
}
