package com.alexdebur.TurMurom.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @NotBlank
//    @Size(min=2, max=20, message = "Логин должен содержать от 2 до 20 символов")
//    private String username;
    @NotBlank
    @Size(min=2, max=20, message = "Фамилия должна содержать от 2 до 20 символов")
    private String lastName;
    @NotBlank
    @Size(min=2, max=20, message = "Имя должно содержать от 2 до 20 символов")
    private String firstName;
//    @Size(min=2, max=20, message = "Логин должен содержать от 2 до 20 символов")
//    private String login;
    @NotBlank
    @Email(message = "Email не корректен")
    @Column(unique = true, updatable = false)
    private String email;
    @NotBlank
    @NotNull(message = "Пароль должен содержать от 6 до 12 символов")
    private String password;
    @Transient
    private String passwordConfirm;
    private boolean active;
    //private String activationCode;
    private String pathPhoto;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

//    public User(String username, String lastName, String firstName, String email, String password) {
//        this.username = username;
//        this.lastName = lastName;
//        this.firstName = firstName;
//        this.email = email;
//        this.password = password;
//    }

    // security config

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}