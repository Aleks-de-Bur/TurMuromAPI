package com.alexdebur.TurMurom.Services;

import com.alexdebur.TurMurom.Models.User;
import com.alexdebur.TurMurom.Models.Role;
import com.alexdebur.TurMurom.Models.UserRegistrationDto;
import com.alexdebur.TurMurom.Repositories.RoleRepository;
import com.alexdebur.TurMurom.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    public User save(UserRegistrationDto registrationDto);
    public List<User> getAll();
}




//    @Autowired
//    public void setRepository(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public Optional<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }
//
//    public void insertUser(User user) {
//        userRepository.save(user);
//    }
//
//    public void deleteUserById(Long id) {
//        userRepository.deleteById(id);
//    }
