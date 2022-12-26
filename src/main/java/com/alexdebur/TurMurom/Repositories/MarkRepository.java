package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Category;
import com.alexdebur.TurMurom.Models.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByTitleAndDescription(String title, String description);

}
