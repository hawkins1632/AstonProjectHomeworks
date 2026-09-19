package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toResponse(User user);

    List<UserResponseDto> toResponseList(List<User> userList);

    void updateEntity(@MappingTarget User user, UserRequestDto request);

    User toEntity(UserRequestDto request);
}
