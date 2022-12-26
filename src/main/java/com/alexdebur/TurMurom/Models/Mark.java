package com.alexdebur.TurMurom.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "mark")
    private List<MarkPhoto> markPhotos;
    @OneToMany(mappedBy = "mark")
    private List<Schedule> schedules;

}
