package org.example.mapper;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    UserResponseDto toResponse(User user);

    List<UserResponseDto> toResponseList(List<User> userList);

    User toEntity(UserRequestDto request);
}
