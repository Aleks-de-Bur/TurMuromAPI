package com.alexdebur.TurMurom.Models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition="TEXT")
    private String description;
    private String duration;
    private String pathPhoto;

//    @ManyToMany
//    @JsonManagedReference
//    private List<Mark> marks;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private Set<RouteMark> routeMarks;

    public Route(String title, String description, String duration, String pathPhoto, RouteMark... routeMarks) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.pathPhoto = pathPhoto;
        for(RouteMark routeMark : routeMarks) routeMark.setRoute(this);
        this.routeMarks = Stream.of(routeMarks).collect(Collectors.toSet());
    }
}