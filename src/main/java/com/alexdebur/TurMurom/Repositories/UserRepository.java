package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.RegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<RegisterEntity, Long> {
}
