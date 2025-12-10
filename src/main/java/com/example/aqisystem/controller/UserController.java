package com.example.aqisystem.controller;

import com.example.aqisystem.model.User;
import com.example.aqisystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}
