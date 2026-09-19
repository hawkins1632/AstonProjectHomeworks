package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Сущность пользователя.
 * <p>
 * Содержит основную информацию о пользователе:
 * имя, электронную почту, возраст и дату создания в бд.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    /** Уникальный идентификатор пользователя. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Имя пользователя. */
    @Column(nullable = false, length = 100)
    private String name;

    /** Электронная почта пользователя. */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Возраст пользователя. */
    @Column(nullable = false)
    private Integer age;

    /** Дата создания записи в БД. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Создаёт нового пользователя.
     *
     * @param name  имя пользователя
     * @param email электронная почта пользователя
     * @param age   возраст пользователя
     */
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /**
     * Устанавливает дату создания перед сохранением пользователя в БД.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

