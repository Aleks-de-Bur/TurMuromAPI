package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.ExcursionPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ExcursionPhotoRepository extends JpaRepository<ExcursionPhoto, Long> {

}
