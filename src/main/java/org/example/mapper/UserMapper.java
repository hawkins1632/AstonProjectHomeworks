package org.example.mapper;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;
public class UserMapper {
    public static UserResponseDto toResponseDto(User user){
        if (user == null){
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }

    public static User toEntity(UserRequestDto requestDto){
        if (requestDto == null){
            return null;
        }

        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setAge(requestDto.getAge());
        return user;
    }
}
