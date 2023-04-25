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
public class UserElectedMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mark_id")
    private Mark mark;

    public UserElectedMark(User user, Mark mark) {
        this.user = user;
        this.mark = mark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserElectedMark)) return false;
        UserElectedMark that = (UserElectedMark) o;
        return Objects.equals(user.getEmail(), that.user.getEmail()) &&
                Objects.equals(mark.getTitle(), that.mark.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getEmail(), mark.getTitle());
    }
}
