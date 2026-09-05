package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность пользователя.
 *
 * Содержит основную информацию о пользователе:
 * имя, электронную почту, возраст и дату создания в бд.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Создаёт нового опльзователя.
     *
     * @param name имя пользователя
     * @param email электронная почта пользователя
     * @param age возраст пользователя
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

    @Override
    public String toString() {
        return String.format("User | ID: %d | Name: %s | Email: %s | Age: %d | Created: %s",
                id, name, email, age, createdAt);
    }

    /**
     * Сравнивает пользователей по их идентификатору и основным данным
     *
     * @param object объект для сравнения
     * @return {@code true}, если пользователи равны
     */
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) && Objects.equals(age, user.age) &&
                Objects.equals(createdAt, user.createdAt);
    }

    /**
     * Возвращает хеш код пользователя на основе полей, используемых в {@link #equals(Object).}
     *
     * @return хеш код пользователя
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, createdAt);
    }
}

