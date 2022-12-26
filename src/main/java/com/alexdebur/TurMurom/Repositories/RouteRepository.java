package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RouteRepository extends JpaRepository<Route, Long> {
}
