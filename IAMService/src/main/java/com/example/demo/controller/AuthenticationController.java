package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.AuthenticateService;
import com.example.demo.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("v2")
public class AuthenticationController {
    @Autowired
    private AuthenticateService authenticateService;

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")

    public ResponseEntity<ResponseObject> register(
            @RequestBody Register request
    ) {
        return ResponseEntity.ok(registerService.register(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ResponseObject> confirmEmail(
            @RequestBody ConfirmRequest confirmRequest
    ) {
        return ResponseEntity.ok(registerService.confirmEmail(confirmRequest));
    }

    @PostMapping("/sendToken")
    public ResponseEntity sendToken(
            @RequestBody EmailRequest request
    ) throws MessagingException, UnsupportedEncodingException {
        return ResponseEntity.ok(registerService.sendToken(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> authenticate(
            @RequestBody AuthenticateRequest request
    ) {
        return ResponseEntity.ok(authenticateService.authenticate(request));
    }


}
