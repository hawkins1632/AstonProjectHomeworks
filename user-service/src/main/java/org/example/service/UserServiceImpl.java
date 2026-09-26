package org.example.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.kafka.UserEventProducer;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация интерфейса {@link UserService} для управления бизнес-логикой пользователей.
 * Отвечает за валидацию идентификаторов и персональных данных, обработку бизнес-исключений
 * и взаимодействие со слоем доступа к данным.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;


    /**
     * Создаёт нового пользователя.
     * После успешного сохранения отправляется событие в Kafka.
     *
     * @param requestDto данные пользователя
     * @return созданный пользователь
     */
    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {
        log.info("Создание пользователя: email = {}", requestDto.getEmail());

        User user = userRepository.save(
                userMapper.toEntity(requestDto)
        );
        userEventProducer.sendCreated(user);

        log.info(
                "Пользователь успешно создан: id={}, email={}",
                user.getId(),
                user.getEmail()
        );
        return userMapper.toResponse(user);
    }

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        log.info("Поиск пользователя с id={}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
                    log.warn("Пользователь не найден: id={}", id);
                    return new UserNotFoundException(id);
                });

        log.info(
                "Пользователь найден: id={}, email={}",
                user.getId(),
                user.getEmail()
        );

        return userMapper.toResponse(user);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        log.info("Получение всех пользователей");

        List<User> users = userRepository.findAll();

        log.info("Пользователи найдены: количество={}", users.size());

        return userMapper.toResponseList(users);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id         идентификатор пользователя
     * @param requestDto новые данные
     * @return обновлённый пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        log.info("Обновление пользователя: id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Невозможно обновить пользователя, пользователь не найден: id={}", id);
                    return new UserNotFoundException(id);
                });

        userMapper.updateEntity(user, requestDto);
        User updatedUser = userRepository.save(user);

        log.info(
                "Пользователь успешно обновлен: id={}, email={}",
                updatedUser.getId(),
                updatedUser.getEmail()
        );
        userEventProducer.sendUpdated(user);

        return userMapper.toResponse(updatedUser);
    }

    /**
     * Удаляет пользователя по идентификатору.
     * Сначала пользователь находится по id, чтобы получить его email.
     * Затем пользователь удаляется из базы данных,
     * после чего в Kafka отправляется событие USER_DELETED.
     *
     * @param id идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаление пользователя: id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Невозможно удалить пользователя, пользователь не найден: id={}", id);
                    return new UserNotFoundException(id);
                });

        userRepository.delete(user);
        userEventProducer.sendDeleted(user);
        log.info(
                "Пользователь успешно удален: id={}, email={}",
                user.getId(),
                user.getEmail()
        );
    }
}