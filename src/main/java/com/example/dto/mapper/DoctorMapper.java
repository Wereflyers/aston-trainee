package com.example.dto.mapper;

import com.example.dto.DoctorResponseDto;
import com.example.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * The interface Doctor mapper.
 */
@Mapper
public interface DoctorMapper {
    /**
     * The constant INSTANCE.
     */
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    /**
     * Doctor to doctor dto.
     *
     * @param doctor the doctor
     * @return the doctor dto
     */
    DoctorResponseDto doctorToDoctorDto(Doctor doctor);

    /**
     * Doctor list to a dto list.
     *
     * @param doctorList the doctor list
     * @return the list
     */
    List<DoctorResponseDto> doctorListToDto(List<Doctor> doctorList);
}