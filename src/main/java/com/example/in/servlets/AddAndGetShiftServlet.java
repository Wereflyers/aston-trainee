package com.example.in.servlets;

import com.example.aop.annotations.Loggable;
import com.example.dto.ShiftDto;
import com.example.dto.mapper.ShiftMapper;
import com.example.exceptions.NoRightsException;
import com.example.exceptions.WrongDataException;
import com.example.model.Shift;
import com.example.service.AuthService;
import com.example.service.DoctorService;
import com.example.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDate;

@Loggable
@WebServlet(urlPatterns = "/shifts")
public class AddAndGetShiftServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private ScheduleService scheduleService;
    private AuthService authService;
    private DoctorService doctorService;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        this.scheduleService = new ScheduleService();
        this.authService = new AuthService();
        this.doctorService = new DoctorService();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        getValidUsername(req);

        InputStream data = req.getInputStream();
        ShiftDto shiftDto = objectMapper.readValue(data, ShiftDto.class);
        validateShift(shiftDto);

        Shift result = scheduleService.addShift(ShiftMapper.INSTANCE.shiftDtoToShift(shiftDto));
        resp.setStatus(HttpServletResponse.SC_CREATED);
        String json = objectMapper.writeValueAsString(ShiftMapper.INSTANCE.shiftToDto(result));
        PrintWriter out = resp.getWriter();
        out.write(json);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String username;
        try {
            username = req.getHeader("username");
        } catch (NullPointerException e) {
            throw new WrongDataException("Введите имя пользователя");
        }
        if (username == null || username.isBlank() || authService.hasNoUser(username)) {
            throw new NoRightsException("Вы имеете недостаточно прав для выполнения данной операции");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(req.getParameter("date"));
        } catch (NullPointerException e) {
            date = LocalDate.now();
        }
        String doctor = getDoctor(req, username);

        Shift shift = scheduleService.getShift(doctor, date);
        String result = objectMapper.writeValueAsString(ShiftMapper.INSTANCE.shiftToDto(shift));
        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();
        out.write(result);
    }

    private String getDoctor(HttpServletRequest req, String username) {
        String doctor;
        try {
            doctor = req.getParameter("doctor");
        } catch (NullPointerException e) {
            if (username.equals("admin")) {
                throw new WrongDataException("Введите имя врача");
            } else {
                doctor = username;
            }
        }
        if (doctor == null || doctor.isBlank() || doctorService.hasNoDoctorByName(doctor)) {
            throw new WrongDataException("Некорректное имя врача");
        }
        return doctor;
    }

    private void validateShift(ShiftDto shiftDto) {
        if (shiftDto.getDoctorName().isBlank()) {
            throw new WrongDataException("Некорректное имя врача");
        }
        if (shiftDto.getOffices().isEmpty()) {
            throw new WrongDataException("Добавьте кабинеты");
        }
    }

    private void getValidUsername(HttpServletRequest req) {
        String username;
        try {
            username = req.getHeader("username");
        } catch (NullPointerException e) {
            throw new WrongDataException("Введите имя пользователя");
        }
        if (!username.equals("admin")) {
            throw new NoRightsException("Вы имеете недостаточно прав для выполнения данной операции");
        }
    }
}



