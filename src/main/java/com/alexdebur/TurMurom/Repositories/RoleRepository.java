package com.alexdebur.TurMurom.Repositories;

import com.alexdebur.TurMurom.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}