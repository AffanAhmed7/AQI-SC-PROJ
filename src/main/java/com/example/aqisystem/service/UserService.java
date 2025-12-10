package com.example.aqisystem.service;

import com.example.aqisystem.model.User;
import com.example.aqisystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public User registerUser(User user) {
        return userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUser(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }
}
