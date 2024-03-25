package com.example.dto.mapper;

import com.example.dto.UserDto;
import com.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * The interface User mapper.
 */
@Mapper
public interface UserMapper {
    /**
     * The constant INSTANCE.
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * User dto to user user.
     *
     * @param userDto the user dto
     * @return the user
     */
    User userDtoToUser(UserDto userDto);
}
