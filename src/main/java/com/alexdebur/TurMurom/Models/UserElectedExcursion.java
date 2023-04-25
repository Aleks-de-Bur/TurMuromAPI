package com.alexdebur.TurMurom.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserElectedExcursion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "excursion_id")
    private Excursion excursion;

    public UserElectedExcursion(User user, Excursion excursion) {
        this.user = user;
        this.excursion = excursion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserElectedExcursion)) return false;
        UserElectedExcursion that = (UserElectedExcursion) o;
        return Objects.equals(user.getEmail(), that.user.getEmail()) &&
                Objects.equals(excursion.getTitle(), that.excursion.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getEmail(), excursion.getTitle());
    }
}
