package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.RouteMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteMarkRepository extends JpaRepository<RouteMark, Long> {
    List<RouteMark> findByRouteId(Long id);
    Optional<RouteMark> findByMarkId(Long id);

    @Query("DELETE FROM RouteMark WHERE id = :id")
    void deleteM(@Param("id") Long id);
}
