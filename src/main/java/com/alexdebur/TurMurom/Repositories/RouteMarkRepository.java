package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.RouteMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface RouteMarkRepository extends JpaRepository<RouteMark, Long> {
    List<RouteMark> findByRouteId(Long id);
    Optional<RouteMark> findByMarkId(Long id);
}
