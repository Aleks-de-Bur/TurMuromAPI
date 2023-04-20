package com.alexdebur.TurMurom.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

public enum Role implements GrantedAuthority {
    ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR, ROLE_GUIDE;

    @Override
    public String getAuthority() {
        return name();
    }
}