package ru.itgirl.libraryproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.itgirl.libraryproject.dto.RegistrationRequest;
import ru.itgirl.libraryproject.model.AppUser;
import ru.itgirl.libraryproject.repository.AppUserRepository;


@RestController // Обозначает, что класс является REST контроллером
@RequestMapping("/api") // Базовый путь для всех эндпоинтов в контроллере
public class RegistrationController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(AppUserRepository userRepository, PasswordEncoder  passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Эндпоинт для регистрации нового пользователя
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
        // Проверка: существует ли уже пользователь с таким именем
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с таким именем уже существует.");
        }

        // Создаем нового пользователя и шифруем пароль
        AppUser newUser = new AppUser();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Задаём базовую роль для зарегистрированного пользователя
        newUser.setRoles("ROLE_USER");

        // Сохраняем пользователя в базе данных PostgreSQL
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Пользователь успешно зарегистрирован");
    }
}

