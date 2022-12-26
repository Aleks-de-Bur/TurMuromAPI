package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
