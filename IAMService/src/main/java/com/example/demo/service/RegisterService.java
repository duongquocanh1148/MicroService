package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repositories.TokenRepository;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final GmailService gmailService;
    private String jwt;

    public ResponseObject register(Register request){

        Users check = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (check == null) {
            if (validateEmail(request.getEmail()) && validateUserName(request.getUserName()) && validatePassword(request.getPassword())) {
                var user = Users.builder()
                        .userName(request.getUserName())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .email(request.getEmail())
                        .doB(request.getDoB())
                        .address(request.getAddress())
                        .mobile(request.getMobile())
                        .isConfirm(false)
                        .build();
                userRepository.save(user);
                return ResponseObject.builder()
                        .status("OK")
                        .message("Please confirm your account!")
                        .data(user)
                        .build();
            } else {
                return ResponseObject.builder()
                        .status("Failed")
                        .message("Please check your information input!")
                        .data("userName: "
                                + validateUserName(request.getUserName()) + " | email: "
                                + validateEmail(request.getEmail()) + " | password: "
                                + validatePassword(request.getPassword()))
                        .build();
            }

        }
        return ResponseObject.builder()
                .status("Failed")
                .message("Email already exist!")
                .build();
    }

    public ResponseObject confirmEmail(ConfirmRequest confirmRequest){
        Users user = userRepository.findByEmail(confirmRequest.getEmail()).orElse(null);
        if(user != null){
            if(user.getToken() != null){
                if (user.getToken().equals(confirmRequest.getToken())) {
                    userRepository.findById(user.getId()).map(users -> {
                        users.setConfirm(true);
                        return userRepository.save(users);
                    });
                    return ResponseObject.builder()
                            .status("OK")
                            .message("Confirm successfully")
                            .data("isConfirm: " + userRepository.findById(user.getId()).get().isConfirm())
                            .build();
                }
                return ResponseObject.builder()
                        .status("Failed")
                        .message("Please check your token again!")
                        .data("isConfirm: " + userRepository.findById(user.getId()).get().isConfirm())
                        .build();
            }
            return ResponseObject.builder()
                    .status("Failed")
                    .message("Please check your token again!")
                    .data("isConfirm: " + userRepository.findById(user.getId()).get().isConfirm())
                    .build();

        }
        return ResponseObject.builder()
                .status("Failed")
                .message("Please check your email again!")
                .data("isConfirm: " + userRepository.findById(user.getId()).get().isConfirm())
                .build();
    }
    public ResponseObject sendToken(EmailRequest emailRequest) throws MessagingException, UnsupportedEncodingException {
        String subject = "Please confirm your account!";
        String body = "Here is your token: ";
        Users user = userRepository.findByEmail(emailRequest.getEmail()).orElse(null);
        if(user != null){
            jwt = jwtService.generateToken(user);
            //saveUserToken(user, jwt);
            userRepository.findById(user.getId()).map(users -> {
                users.setToken(jwt);
                return userRepository.save(users);
            });
            gmailService.SendMail(user.getEmail(), subject,"Attention: Token is valid for only 5 minutes\n" +  body + " " + jwt);
            return ResponseObject.builder()
                    .status("OK")
                    .message("Check your email and confirm!")
                    .data("token: " + user.getToken())
                    .build();
        }
        return ResponseObject.builder()
                .status("Failed")
                .message("Please enter your email!")
                .data("token: " + jwt)
                .build();
    }

    boolean validateEmail(String value) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    boolean validateUserName(String value) {
        String regexPattern = "^[a-z0-9].{8,32}$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    boolean validatePassword(String value) {
        String regexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.#$@_+,?-]).{8,32}$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    private void saveUserToken (Users user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType("BEARER")
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
