package com.example.mywebsite.service;

import com.example.mywebsite.entity.User;
import com.example.mywebsite.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.core.TreeCodec;
import tools.jackson.databind.ser.jdk.JDKKeySerializers;

import java.security.Principal;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TreeCodec treeCodec;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TreeCodec treeCodec) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.treeCodec = treeCodec;
    }

    public User registerUser(String username, String password, String role) throws Exception{
        if (usernameTaken(username)) {
            throw new Exception("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(role);

        return userRepository.save(user);
    }

    public User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName()).orElseThrow();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean usernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
