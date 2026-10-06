## User-Service

Мультимодальное приложение для управления пользователями и отправки сообщений пользователям.

### Стек технологий
- Java 17
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA / Hibernate
- Spring Validation
- Spring HATEOAS
- PostgreSQL
- Apache Kafka
- Debezium
- Kafka Connect
- MailHog
- SpringDoc OpenAPI
- MapStruct
- Lombok
- JUnit 5
- Mockito
- Testcontainers
- GreenMail
- Awaitility
- JaCoCo
- Maven

## Функциональность

### User Service
- Создание пользователя
- Получение пользователя по id
- Получение списка всех пользователей
- Обновление данных пользователя
- Удаление пользователя
- Валидация входных данных
- Единый формат обработки ошибок
- HATEOAS-ссылки для пользователей и коллекции пользователей
- Публикация событий о создании, изменении и удалении пользователя

### Notification Service
- Получение событий пользователей из Kafka
- Отправка email-уведомлений при создании пользователя
- Отправка email-уведомлений при изменении пользователя
- Отправка email-уведомлений при удалении пользователя
- Ручная отправка уведомления через REST API
- Проверка повторной обработки Kafka-событий

### Модули проекта
```
User-Service
├── common                # Общие модели и события проекта
├── user-service          # REST API и работа с пользователями
└── notification-service  # Обработка событий и отправка уведомлений
```

## Архитектура взаимодействия

События пользователя передаются между сервисами через Kafka.
```
user-service
     │
     │ изменение пользователя
     ▼
PostgreSQL
     │
     │ outbox_events
     ▼
Debezium / Kafka Connect
     │
     │ user-events
     ▼
Kafka
     │
     ▼
notification-service
     │
     ▼
MailHog / Email
```
Для публикации событий используется паттерн Transactional Outbox.

При создании, изменении или удалении пользователя в рамках одной транзакции:

 1. изменяются данные пользователя;
 2. создаётся запись в таблице outbox_events.

После успешного commit Debezium отслеживает изменения таблицы outbox_events и публикует событие в Kafka topic user-events.

Событие содержит:
```
{
  "eventId": "uuid",
  "id": 1,
  "email": "user@example.com",
  "type": "CREATED"
}
```
Поддерживаемые типы событий:
```
CREATED
UPDATED
DELETED
```
notification-service дополнительно сохраняет идентификаторы обработанных событий в таблице processed_events, что позволяет избежать повторной обработки одного и того же события.

## HATEOAS

user-service поддерживает HATEOAS и возвращает пользователей в формате application/hal+json.

Для одиночного пользователя используется:

EntityModel<UserResponseDto>

Для списка пользователей:

CollectionModel<EntityModel<UserResponseDto>>

Пример ответа для одного пользователя:
```
{
  "id": 1,
  "name": "Ivan",
  "email": "ivan@test.com",
  "age": 30,
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/users/1"
    },
    "users": {
      "href": "http://localhost:8080/api/users"
    }
  }
}
```
Пример ответа коллекции:
```
{ 
	"_embedded": { 
		"userResponseDtoList": [ 
		   {
			 "id": 1,
			 "name": "Ivan",
			 "email": "ivan@test.com",
			 "age": 30, 
			 "_links": {
			     "self": { 
              "href": "http://localhost:8080/api/users/1"
            }, 
           "users": { 
				      "href": "http://localhost:8080/api/users" 
				    } 
        } 
     } 
     ] 
    }, 
	     "_links": { 
		"self": { 
		  "href": "http://localhost:8080/api/users" 
		  } 
    } 
	}
```
HATEOAS-логика вынесена в отдельный UserModelAssembler, поэтому бизнес-логика UserService не зависит от формирования ссылок.

## REST API
### User Service

Base URL:

http://localhost:8080

Основные endpoint'ы:
```
POST   /api/users
GET    /api/users
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
```
Ответы GET/POST/PUT для пользователей используют application/hal+json.

### Notification Service

Base URL:

http://localhost:7070

Endpoint ручной отправки уведомления:

POST /api/notification/email

Пример запроса:
```
{
  "email": "user@example.com",
  "type": "CREATED"
}
```
При успешной обработке возвращается:

202 Accepted

## Swagger / OpenAPI

### User Service

Swagger UI:

http://localhost:8080/swagger-ui/index.html

OpenAPI:

http://localhost:8080/v3/api-docs

Документация содержит:

- CRUD endpoint'ы;
- request и response модели;
- коды HTTP-ответов;
- ошибки валидации;
- 400 Bad Request;
- 404 Not Found;
- 409 Conflict;
- HATEOAS response-модели;
- application/hal+json.

Для HATEOAS используются схемы:
```
EntityModelUserResponseDto
CollectionModelEntityModelUserResponseDto
```
### Notification Service

Swagger UI:

http://localhost:7070/swagger-ui.html

OpenAPI:

http://localhost:7070/v3/api-docs

Статическая OpenAPI-спецификация находится в:

notification-service/src/main/resources/static/openapi.yml

Она используется также для генерации API-интерфейсов через OpenAPI Generator.

## Запуск проекта
### Запуск инфраструктуры

Для локального запуска необходимо поднять Docker-контейнеры:

docker compose up -d

Docker Compose запускает:
```
Kafka
Kafka UI
PostgreSQL
pgAdmin
MailHog
Kafka Connect / Debezium
```
Основные порты:
```
User Service       8080
Notification       7070
PostgreSQL         5433
Kafka              9094
Kafka UI           8070
Kafka Connect      8083
pgAdmin            5050
MailHog            8025
MailHog SMTP       1025
```
### Запуск User Service

mvn -pl user-service spring-boot:run

### Запуск Notification Service

mvn -pl notification-service spring-boot:run

### Сборка проекта

mvn clean install

### Тестирование

Проект содержит unit-, web-, интеграционные и E2E-тесты.

Проверяются:

- CRUD-функциональность user-service;
- валидация запросов;
- обработка исключений;
- HATEOAS-ссылки;
- OpenAPI-документация;
- доступность Swagger UI;
- отправка email;
- обработка Kafka-событий;
- Transactional Outbox;
- интеграция с Debezium;
- идемпотентность обработки событий.

Запуск всех тестов:

mvn test

Для интеграционных и E2E-тестов требуется Docker, поскольку используются Testcontainers.

## Отчёт о покрытии

JaCoCo создаёт отчёты для модулей проекта в:
```
user-service/target/site/jacoco/index.html
notification-service/target/site/jacoco/index.html
```
## Техническое задание
Добавление Swagger-документации и HATEOAS в API.

- Задокументировать существующее API (из задания 4) с помощью Swagger (Springdoc OpenAPI), чтобы можно было легко изучить и тестировать API через веб-интерфейс.
- Добавить поддержку HATEOAS, чтобы API предоставляло ссылки для навигации по ресурсам.

## Стратегия слияния веток (Git Strategy)
Команда утвердила стратегию Merge commit для сохранения прозрачной истории интеграции фич.

## Состав команды и распределение задач
Морозов Павел:
- Улучшить Swagger-документацию user-service
- Провести финальную интеграционную проверку проекта

Мизина Диана:
- Дополнить Javadoc и унифицировать документацию проекта
- Реализовать Transactional Outbox для публикации событий в Kafka

Чалапко Матвей (Team Lead на это задание):
- Проверить и привести в порядок OpenAPI-документацию notification-service
- Добавить HATEOAS в user-service + Проверить совместимость Swagger и HATEOAS
