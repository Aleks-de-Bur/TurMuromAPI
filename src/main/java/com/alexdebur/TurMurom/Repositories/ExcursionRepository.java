package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Excursion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ExcursionRepository extends JpaRepository<Excursion, Long> {
    Page<Excursion> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
