package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.RegisterEntity;
import com.alexdebur.TurMurom.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorizationService {
    private UserRepository userRepository;

    @Autowired
    public void setRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<RegisterEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<RegisterEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void insertUser(RegisterEntity user) {
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
