# RouteMoodServer

Серверное приложение для работы с маршрутами, рейтингами и пользователями. Основано на Spring Boot и полностью контейнеризовано с помощью Docker.

## Требования

- Docker и Docker Compose
- Git
- Скрипт `run-docker.sh` должен быть исполняемым (`chmod +x run-docker.sh`)

## Быстрый старт

Этот проект использует Docker Compose для управления всеми сервисами, включая приложение, базу данных и pgAdmin.

### 1. Клонирование репозитория

```sh
git clone https://github.com/RouteMood/RouteMoodServer.git
cd RouteMoodServer
```

### 2. Запуск

Для запуска всех сервисов используйте скрипт `run-docker.sh`, передав ему путь к файлу с токеном для GPT.

```sh
./run-docker.sh <token_file> [--build]
```

Скрипт выполнит следующие действия:
1.  Прочитает токен из указанного файла.
2.  Остановит и удалит любые запущенные контейнеры.
3.  Запустит `docker-compose up`, который соберет образ приложения и поднимет все сервисы.

Чтобы пересобрать образ приложения, добавьте флаг `--build`

### Доступ к сервисам

- **Сервер**: `http://localhost:8080`
- **PostgreSQL**: порт `5432`
- **pgAdmin**: `http://localhost:5050` (логин: `pgadmin4@pgadmin.org`, пароль: `admin`)

## Тестирование

Для локального запуска тестов отдельно используйте:

```sh
./gradlew test
```

## Структура Docker

- **`Dockerfile`**: Многостадийный файл для сборки и запуска Java-приложения.
- **`docker/docker-compose.yaml`**: Определяет сервисы `routemood-server`, `postgres` и `pgadmin`.
- **`run-docker.sh`**: Управляющий скрипт для удобного запуска и остановки окружения.
- **`generate-config.sh`**: Скрипт для генерации конфигурации, используется внутри `Dockerfile`.

## CI

- Все тесты автоматически запускаются в GitHub Actions при каждом коммите в ветку `main`.
- Проверяется сборка проекта с помощью Gradle.

## Основные зависимости

- Spring Boot
- PostgreSQL, H2 (для тестов)
- Spring Security, JWT
- Lombok, JUnit, Mockito
- Google API Client, OkHttp, Gson

## Лицензия

См. файл [LICENSE](LICENSE). 