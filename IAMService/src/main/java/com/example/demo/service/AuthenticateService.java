package com.example.demo.service;


import com.example.demo.model.AuthenticateRequest;
import com.example.demo.model.ResponseObject;
import com.example.demo.model.Token;
import com.example.demo.model.Users;
import com.example.demo.repositories.TokenRepository;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private String jwt;

    public ResponseObject authenticate(AuthenticateRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            Users user = userRepository.findByUserName(request.getUserName())
                    .orElse(null);
            if (user != null) {
                if (user.isConfirm()==true){
                    jwt = jwtService.generateToken(user);
                    saveUserToken(user, jwt);
                    return ResponseObject.builder()
                            .status("OK")
                            .message("Login successfully")
                            .data(jwt)
                            .build();
                } else return ResponseObject.builder()
                        .status("Failed")
                        .message("Tài khoản chưa được kích hoạt.Vui lòng xác minh email")
                        .data(null)
                        .build();

            } else
                return ResponseObject.builder()
                    .status("Failed")
                    .message("doi tuong rong")
                    .data("rong")
                    .build();
        } catch (Exception e) {
            return ResponseObject.builder()
                    .status("Failed")
                    .message("Login fail please check username anh password")
                    .data(e.getMessage())
                    .build();
        }

//        return ResponseObject.builder()
//                .status("Failed")
//                .message("Login fail")
//                .build();
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

        private void revokeAllUserTokens (Users user){
            var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
            if (validUserTokens.isEmpty())
                return;
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

