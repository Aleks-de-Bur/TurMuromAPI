package com.alexdebur.TurMurom.ApiControllers;

import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NavigationControllerAPI {
    private final UserRepository userRepository;

    public NavigationControllerAPI(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/autorizedUser{email}")
    public User getUser(@PathVariable String email){
        return userRepository.findByEmail(email);
    }
}
