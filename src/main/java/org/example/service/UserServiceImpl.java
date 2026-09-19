package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация интерфейса {@link UserService} для управления бизнес-логикой пользователей.
 * Отвечает за валидацию идентификаторов и персональных данных, обработку бизнес-исключений
 * и прямое взаимодействие со слоем доступа к данным через.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {
        return userMapper.toResponse(userRepository.save(userMapper.toEntity(requestDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        return userMapper.toResponse(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntity(user, requestDto);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsUserById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
