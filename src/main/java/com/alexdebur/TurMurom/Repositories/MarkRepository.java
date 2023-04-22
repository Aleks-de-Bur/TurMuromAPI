package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

//@Component
//public interface MarkRepository extends JpaRepository<Mark, Long> {
//    Optional<Mark> findByTitleAndDescription(String title, String description);
//}
@Repository
@Transactional
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Page<Mark> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Optional<Mark> findByTitleAndDescription(String title, String description);
}
