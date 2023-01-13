package com.alexdebur.TurMurom.Models;

import com.alexdebur.TurMurom.WorkClasses.InteractionPhoto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ManyToOne
    @JsonBackReference
    private Category category;

    private String address;
    private Boolean elected;

    //    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "mark", cascade = CascadeType.ALL)
    private List<MarkPhoto> markPhotos;

    //    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "mark", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @ManyToMany
    @JsonManagedReference
    private List<Route> routes;

    public void setMarkPhotos(List<MarkPhoto> markPhotos) {

        this.markPhotos = markPhotos;
    }
}
