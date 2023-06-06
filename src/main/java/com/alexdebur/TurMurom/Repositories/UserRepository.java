package com.alexdebur.TurMurom.Repositories;

import java.util.Optional;

import com.alexdebur.TurMurom.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByGuideId(Long guideId);
}