package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Primljen zahtev za kreiranje korisnika: {}", user.getEmail());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("Pretraga korisnika sa ID: {}", id);
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
