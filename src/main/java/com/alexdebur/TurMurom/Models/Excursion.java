package com.alexdebur.TurMurom.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Excursion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition="TEXT")
    private String description;
    private String duration;
    private Integer price;
    @ManyToOne
    @JsonBackReference
    private Guide guide;

    @OneToMany(mappedBy = "excursion", cascade = CascadeType.ALL)
    private Set<UserElectedExcursion> userElectedExcursions = new HashSet<>();

    @OneToMany(mappedBy = "excursion", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ExcursionPhoto> excursionPhotos;
}
