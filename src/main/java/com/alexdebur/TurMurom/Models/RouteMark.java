package com.alexdebur.TurMurom.Models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RouteMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mark_id")
    private Mark mark;

    private Integer ordinal;

    public RouteMark(Mark mark, Route route, Integer ordinal) {
        this.route = route;
        this.mark = mark;
        this.ordinal = ordinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteMark)) return false;
        RouteMark that = (RouteMark) o;
        return Objects.equals(route.getTitle(), that.route.getTitle()) &&
                Objects.equals(mark.getTitle(), that.mark.getTitle()) &&
                Objects.equals(ordinal, that.ordinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route.getTitle(), mark.getTitle(), ordinal);
    }
}
