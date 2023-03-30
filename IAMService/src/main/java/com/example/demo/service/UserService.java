package com.example.demo.service;

import com.example.demo.model.Users;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public List<Users> getAllUser(){
        return userRepository.findAll();
    }
}
