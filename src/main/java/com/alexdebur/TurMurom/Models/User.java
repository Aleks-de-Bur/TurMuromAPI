package com.alexdebur.TurMurom.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min=2, max=20, message = "Фамилия должна содержать от 2 до 20 символов")
    private String lastName;
    @Size(min=2, max=20, message = "Имя должно содержать от 2 до 20 символов")
    private String firstName;
//    @Size(min=2, max=20, message = "Логин должен содержать от 2 до 20 символов")
//    private String login;
    @Email(message = "Email не корректен")
    private String email;
    @NotNull(message = "Пароль должен содержать от 6 до 12 символов")
    private String password;
    @Transient
    private String passwordConfirm;
    private String pathPhoto;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User(String lastName, String firstName, String email, String password, Collection<Role> roles) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
