package com.alexdebur.TurMurom.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition="TEXT")
    private String description;

    @ManyToOne
    @JsonBackReference
    private Category category;

    private String address;
    private String axisX;
    private String axisY;
    private Boolean elected;

    //    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "mark", cascade = CascadeType.ALL)
    private List<MarkPhoto> markPhotos;

    //    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "mark", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

//    @ManyToMany
//    @JsonManagedReference
//    private List<Route> routes;

    @JsonIgnore
    @OneToMany(mappedBy = "mark", cascade = CascadeType.ALL)
    private Set<RouteMark> routeMarks = new HashSet<>();

    @OneToMany(mappedBy = "mark", cascade = CascadeType.ALL)
    private Set<UserElectedMark> userElectedMarks = new HashSet<>();

    public Mark(String title) {
        this.title = title;
    }

    public void setMarkPhotos(List<MarkPhoto> markPhotos) {

        this.markPhotos = markPhotos;
    }
}
