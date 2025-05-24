package ru.itgirl.libraryproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.model.AppUser;
import ru.itgirl.libraryproject.repository.AppUserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository userRepository;
    @Autowired
    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Метод загрузки пользователя по имени для проверки аутентификации
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ищем пользователя в базе
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Разбиваем строку ролей на отдельные элементы и оборачиваем их в SimpleGrantedAuthority
        List<SimpleGrantedAuthority> authorities = Arrays.stream(appUser.getRoles().split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Формируем объект User (из Spring Security) для дальнейшей проверки
        return new User(
                appUser.getUsername(), appUser.getPassword(), appUser.isEnabled(),
                true, // accountNonExpired: учетная запись не просрочена
                true, // credentialsNonExpired: пароль не просрочен
                true, // accountNonLocked: учетная запись не заблокирована
                authorities
        );
    }
}
