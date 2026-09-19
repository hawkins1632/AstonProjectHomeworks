# User-Service

REST-сервис для управления пользователями (CRUD) на Spring Boot.

## Стек технологий
- Java 17
- Spring Boot 4.1.1 (Web, Data JPA, Validation)
- PostgreSQL
- Lombok, MapStruct
- JUnit 5, Mockito, Testcontainers
- Maven, JaCoCo

## Функциональность
- Создание пользователя
- Получение пользователя по id
- Получение списка всех пользователей
- Обновление данных пользователя
- Удаление пользователя

## Основной эндпоинт: `/api/users`

## Структура проекта
```
src/main/java/org/example/
├── controller/ # REST-контроллер 
├── dto/ # Request/Response DTO 
├── exception/ # Исключения и глобальный обработчик 
├── mapper/ # MapStruct маппер 
├── model/ # JPA-сущность User 
├── repository/ # Spring Data JPA репозиторий 
├── service/ # Бизнес-логика и интерфейс 
└── UserServiceApplication.java
```

## Тестирование
Проект покрыт юнит-тестами и интеграционными тестами:
- Юнит-тесты сервиса (`UserServiceImplTest`) — используют Mockito для мокирования зависимостей.
- Интеграционные тесты репозитория (`UserRepositoryTest`) — используют `@DataJpaTest` + Testcontainers для запуска реального PostgreSQL.
- Тесты контроллера (`UserControllerTest`) — используют `@WebMvcTest` и MockMvc.

## Отчёт о покрытии
JaCoCo создаёт отчёт в `target/site/jacoco/index.html` после выполнения mvn test.

# Техническое задание
Добавить в user-service поддержку Spring и разработать API, которое позволит управлять данными.
- Использовать необходимые модули spring(boot, web, data etc).
- Реализовать api для получения, создания, обновления и удаления юзера. Важно, entity не должен возвращаться из контроллера, необходимо использовать dto.
- Заменить Hibernate на Spring data JPA.
- Написать тесты для API(можно делать это при помощи mockMvc или других средств)

## Стратегия слияния веток (Git Strategy)
Команда утвердила стратегию Merge commit для сохранения прозрачной истории интеграции фич.

## Состав команды и распределение задач
Морозов Павел (Team Lead на это задание):
- Задача 3: Создание DTO маппера и интерфейса сервиса
- Задача 5: Создание REST API и обработки ошибок
- Задача 6: Тесты для контроллера(MockMvc) и репозитория (тестконтейнеры)
- Задача 7: Обновление Javadoc и ReadMe

Мизина Диана:
- Задача 1: Подключение Spring Boot, настройка зависимостей в pom.xml 
- Задача 2: Создание слоя данных: Spring Data JPA, удаление Hibernate
- Задача 4: Реализация сервиса + тесты
