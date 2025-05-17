package ru.itgirl.libraryproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Уникальное имя пользователя
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    //Зашифрованный пароль (рекомендуется хранить в виде хэша)
    @Column(nullable = false, length = 255)
    private String password;
    @Column(nullable = false, length = 255)
    private String roles;
    @Column(nullable = false)
    private boolean enabled = true; //флаг, указывающий активен ли пользователь

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }
    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
