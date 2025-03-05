package com.example.foodlens.controllers;

import com.example.foodlens.model.User;
import com.example.foodlens.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @PostMapping("/add")
    public ResponseEntity<String> registerNewUser(@RequestBody User user) {
        String result = userService.registerUser(user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String email) {
        Optional<User> user = userService.getProfile(email);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body("User not found");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String emailOrMobile, @RequestParam String password) {
        Optional<User> user = userService.login(emailOrMobile, password);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(400).body("Invalid Credentials");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestParam String email, @RequestBody User updatedUser) {
        String result = userService.updateProfile(email, updatedUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/allUser")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
