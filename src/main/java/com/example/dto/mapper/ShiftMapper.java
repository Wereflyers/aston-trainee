package com.example.dto.mapper;

import com.example.dto.ShiftDto;
import com.example.model.Shift;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * The interface Shift mapper.
 */
@Mapper
public interface ShiftMapper {
    /**
     * The constant INSTANCE.
     */
    ShiftMapper INSTANCE = Mappers.getMapper(ShiftMapper.class);

    /**
     * Shift to dto shift dto.
     *
     * @param shift the shift
     * @return the shift dto
     */
    ShiftDto shiftToDto(Shift shift);

    /**
     * Shift dto to shift.
     *
     * @param shiftDto the shift dto
     * @return the shift
     */
    Shift shiftDtoToShift(ShiftDto shiftDto);

    /**
     * List of shift to a dto list.
     *
     * @param shifts the shifts
     * @return the list
     */
    List<ShiftDto> listOfShiftToDto(List<Shift> shifts);
}