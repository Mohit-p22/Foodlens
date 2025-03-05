package com.example.foodlens.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.foodlens.model.User;
import com.example.foodlens.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(User user) {
        if (!validateUser(user)) {
            return "Invalid input data!";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already in use!";
        }

        if (userRepository.findByMobile(user.getMobile()).isPresent()) {
            return "Mobile number already in use!";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    public Optional<User> getProfile(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> login(String emailOrMobile, String password) {
        Optional<User> userOptional = userRepository.findByEmailOrMobile(emailOrMobile, emailOrMobile);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Verify password with password encoder
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public String updateProfile(String email, User updatedUser) {
        return userRepository.findByEmail(email).map(user -> {
            user.setName(updatedUser.getName() != null ? updatedUser.getName() : user.getName());
            user.setMobile(updatedUser.getMobile() != null ? updatedUser.getMobile() : user.getMobile());
            user.setGender(updatedUser.getGender() != null ? updatedUser.getGender() : user.getGender());
            user.setAge(updatedUser.getAge() != null ? updatedUser.getAge() : user.getAge());
            user.setWeight(updatedUser.getWeight() != null ? updatedUser.getWeight() : user.getWeight());
            user.setHeight(updatedUser.getHeight() != null ? updatedUser.getHeight() : user.getHeight());
            user.setBloodGroup(updatedUser.getBloodGroup() != null ? updatedUser.getBloodGroup() : user.getBloodGroup());
            user.setAllergies(updatedUser.getAllergies() != null ? updatedUser.getAllergies() : user.getAllergies());
            user.setMedicalHistory(updatedUser.getMedicalHistory() != null ? updatedUser.getMedicalHistory() : user.getMedicalHistory());

            userRepository.save(user);
            return "Profile updated successfully!";
        }).orElse("User not found");
    }

    private boolean validateUser(User user) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String mobileRegex = "^[6-9]\\d{9}$";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,20}$";

        return Pattern.matches(emailRegex, user.getEmail()) &&
                Pattern.matches(mobileRegex, user.getMobile()) &&
                Pattern.matches(passwordRegex, user.getPassword());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
