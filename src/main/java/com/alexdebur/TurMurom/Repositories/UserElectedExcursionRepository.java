package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.UserElectedExcursion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserElectedExcursionRepository extends JpaRepository<UserElectedExcursion, Long> {
    List<UserElectedExcursion> findByUserId(Long id);
}
