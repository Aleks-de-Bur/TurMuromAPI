package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface GuideRepository extends JpaRepository<Guide, Long> {
}
