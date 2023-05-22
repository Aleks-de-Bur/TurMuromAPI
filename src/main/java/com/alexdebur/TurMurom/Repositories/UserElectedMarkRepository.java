package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.RouteMark;
import com.alexdebur.TurMurom.Models.UserElectedMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserElectedMarkRepository extends JpaRepository<UserElectedMark, Long> {
    List<UserElectedMark> findByUserId(Long id);
    UserElectedMark findByUserIdAndMarkId(Long userId, Long markId);
}
