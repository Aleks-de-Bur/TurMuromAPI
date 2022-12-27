package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Mark;
import com.alexdebur.TurMurom.Models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByMark(Mark mark);
}
