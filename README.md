# ✂️ Barbershop App

Android-приложение для записи в барбершоп. Позволяет пользователю зарегистрироваться, выбрать филиал, мастера и услугу, и записаться на удобное время.

---

## 📱 Экраны приложения

- **Главная** — приветствие, выбор города и филиала, ближайшая запись
- **Запись** — выбор услуги, мастера, филиала, даты и времени
- **Мои записи** — история и предстоящие записи пользователя
- **Профиль** — личные данные, редактирование профиля, смена пароля

---

## 🛠️ Стек технологий

| Технология | Назначение |
|---|---|
| Java | Основной язык разработки |
| Android SDK (minSdk 24, targetSdk 36) | Платформа |
| Room (SQLite) | Локальная база данных |
| Navigation Component | Навигация между экранами |
| Material Design 3 | UI-компоненты |
| RecyclerView | Списки мастеров, услуг, записей |
| SharedPreferences (SessionManager) | Хранение сессии пользователя |
| ExecutorService | Фоновые операции с БД |

---

## 🗄️ База данных

Приложение использует локальную базу данных Room со следующими таблицами:

- `users` — пользователи (регистрация, авторизация)
- `cities` — города
- `branches` — филиалы барбершопа
- `services` — услуги (стрижка, борода и т.д.)
- `barbers` — мастера
- `appointments` — записи клиентов

---

## 🚀 Сборка и запуск

### Требования

- Android Studio Hedgehog или новее
- JDK 11+
- Android-устройство или эмулятор с API 24+

### Шаги

1. Клонируй репозиторий:
   ```bash
   git clone https://github.com/skeitboarts-boop/barbershop.git
   cd barbershop
   ```

2. Открой проект в **Android Studio**

3. Дождись синхронизации Gradle

4. Нажми **Run ▶** или собери APK через:
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

Готовый debug-APK находится в:
```
app/build/intermediates/apk/debug/app-debug.apk
```

---

## 📁 Структура проекта

```
app/src/main/java/com/example/barbershop/
├── data/
│   └── local/
│       ├── dao/          # DAO-интерфейсы (Room)
│       ├── db/           # AppDatabase — точка входа в БД
│       ├── entity/       # Сущности (таблицы)
│       └── model/        # Вспомогательные модели
├── session/
│   └── SessionManager.java   # Управление сессией через SharedPreferences
├── ui/
│   ├── adapters/         # RecyclerView-адаптеры
│   ├── fragments/        # Главная, Запись, Мои записи, Профиль
│   ├── HomeActivity.java
│   ├── LoginActivity.java
│   ├── MainActivity.java
│   └── RegisterActivity.java
└── utils/
    ├── AppointmentValidator.java
    └── PasswordUtils.java
```

---

## 👤 Автор

**Мелешенков Кирилл** — [@skeitboarts-boop](https://github.com/skeitboarts-boop)
