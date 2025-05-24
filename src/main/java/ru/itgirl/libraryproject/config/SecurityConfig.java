package ru.itgirl.libraryproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import ru.itgirl.libraryproject.service.impl.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


        // Хранение данных InMemory
 //Частично устаревшая версия конфигурации

//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .csrf().disable()
//                .authorizeHttpRequests((authorize) ->
//                authorize.requestMatchers("/book").hasRole("USER")
//                        .requestMatchers("/book/v2").hasRole("ADMIN")
//                        .requestMatchers("/books").hasRole("USER")
//                        .anyRequest().authenticated())
//                   .httpBasic();
//
//        return httpSecurity.build();
//    }
//
//    @Bean
//    UserDetailsService user() {
//        User.UserBuilder users = User.withDefaultPasswordEncoder();
//        UserDetails user = users
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        UserDetails admin = users
//                .username("admin")
//                .password("password")
//                .roles("USER","ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }


   // Современная конфигурация

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/book").hasRole("USER")
//                        .requestMatchers("/book/v2").hasRole("ADMIN")
//                        .requestMatchers("/books").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults()); //включаем HTTP Basic Authentication
//        return http.build();
//    }
//
//    //Регистрация PasswordEncoder для безопасного хранения паролей
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    //Создание пользователей в памяти с использованием безопасного кодирования паролей
//    @Bean
//    UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        UserDetails user = User.builder()
//                .username("user")
//                .password(encoder.encode("password"))
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(encoder.encode("password"))
//                .roles("USER", "ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//        }
//    }



        private final CustomUserDetailsService userDetailsService;

        @Autowired
        public SecurityConfig(CustomUserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
        }

        // Настройка цепочки фильтров безопасности
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    // Определение правил доступа:
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/book").hasRole("USER")
                            .requestMatchers("/book/v2").hasRole("ADMIN")
                            .requestMatchers("/books").hasRole("ADMIN")
                            .requestMatchers("/api/registration", "login", "/css/", "/js/", "/img/**")
                            .permitAll()
                            .anyRequest().authenticated()
                    )
                    .httpBasic(Customizer.withDefaults()) // Включение HTTP Basic Authentication
                    // Отключаем CSRF для простоты взаимодействия с REST API
                    .csrf(csrf -> csrf.disable());

            return http.build();
        }

        // Бин для шифрования паролей с использованием BCrypt
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        // Благодаря наличию бина CustomUserDetailsService Spring Security автоматически использует его для аутентификации.
}

