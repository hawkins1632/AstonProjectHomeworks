package org.example.service;

import event.EventType;
import event.UserEvent;
import org.example.kafka.UserEventProducer;
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

    private final UserEventProducer userEventProducer;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    /**
     * Создаёт нового пользователя.
     *
     * @param requestDto данные пользователя
     * @return созданный пользователь
     */
    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {



        User user = userRepository.save(

                userMapper.toEntity(requestDto)

        );

        userEventProducer.send(
                new UserEvent(
                        EventType.USER_CREATED, user.getEmail()
                )
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
        return userMapper.toResponse(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userMapper.toResponseList(userRepository.findAll());
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
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntity(user, requestDto);
        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     * сначала пользователь находится по id,
     * затем удаляется из базы данных,
     * после чего в Kafka отправляется событие USER_DELETED,
     * содержащее его email
     */
    @Override
    @Transactional
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id)
                );

        userRepository.deleteById(id);

        userEventProducer.send(
                new UserEvent( EventType.USER_DELETED, user.getEmail()
                )
        );

    }
}
