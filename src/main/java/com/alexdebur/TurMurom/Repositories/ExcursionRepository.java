package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Excursion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ExcursionRepository extends JpaRepository<Excursion, Long> {
}
