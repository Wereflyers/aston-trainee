package com.example.in.servlets;

import com.example.aop.annotations.Loggable;
import com.example.dto.DoctorDto;
import com.example.dto.DoctorResponseDto;
import com.example.dto.mapper.DoctorMapper;
import com.example.exceptions.NoRightsException;
import com.example.exceptions.WrongDataException;
import com.example.model.Doctor;
import com.example.service.AuthService;
import com.example.service.DoctorService;
import com.example.service.SpecialtyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

@Loggable
@WebServlet(urlPatterns = "/doctor")
public class AddAndGetDoctorServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private DoctorService doctorService;
    private AuthService authService;
    private SpecialtyService specialtyService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.objectMapper = new ObjectMapper();
        this.authService = new AuthService();
        this.specialtyService = new SpecialtyService();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String username = getValidUsername(req);
        if (!username.equals("admin")) {
            throw new NoRightsException("Вы имеете недостаточно прав для выполнения данной операции");
        }

        InputStream data = req.getInputStream();
        DoctorDto doctorDto = objectMapper.readValue(data, DoctorDto.class);
        validateDoctor(doctorDto);

        Doctor result = doctorService.addDoctor(doctorDto.getName(), doctorDto.getSpecialty());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        String json = objectMapper.writeValueAsString(DoctorMapper.INSTANCE.doctorToDoctorDto(result));
        PrintWriter out = resp.getWriter();
        out.write(json);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = getValidUsername(req);
        if (username == null || authService.hasNoUser(username)) {
            throw new NoRightsException("Вы имеете недостаточно прав для выполнения данной операции");
        }

        List<DoctorResponseDto> doctors = DoctorMapper.INSTANCE.doctorListToDto(
                doctorService.getAll());
        String result = objectMapper.writeValueAsString(doctors);
        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();
        out.write(result);
    }

    private void validateDoctor(DoctorDto doctorDto) {
        if (doctorDto.getName().isBlank()) {
            throw new WrongDataException("Некорректное имя");
        }
        if (specialtyService.hasNoSpecialtyById(doctorDto.getSpecialty())) {
            throw new WrongDataException("Некорректная специальность");
        }
    }

    private String getValidUsername(HttpServletRequest req) {
        String username;
        try {
            username = req.getHeader("username");
        } catch (NullPointerException e) {
            throw new WrongDataException("Введите имя пользователя");
        }
        return username;
    }
}



