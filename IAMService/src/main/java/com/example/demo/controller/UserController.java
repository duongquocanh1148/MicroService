package com.example.demo.controller;


import com.example.demo.model.Users;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("v1")

public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    @RolesAllowed("admin")
    public ResponseEntity<List<Users>> getAllUser(){
        return  ResponseEntity.ok(userService.getAllUser());
    }


}
