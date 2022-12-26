package com.alexdebur.TurMurom.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String email;
    private String telNumber;
    private String pathPhoto;
    @OneToMany(mappedBy = "guide")
    @JsonManagedReference
    private List<Excursion> excursions;
}
