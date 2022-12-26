package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
