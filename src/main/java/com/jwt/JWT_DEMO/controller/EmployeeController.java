package com.jwt.JWT_DEMO.controller;

import com.jwt.JWT_DEMO.config.SpringSecurity;
import com.jwt.JWT_DEMO.model.*;
import com.jwt.JWT_DEMO.repository.EmployeeRepository;
import com.jwt.JWT_DEMO.service.RefreshTokenService;
import com.jwt.JWT_DEMO.service.UserService;
import com.jwt.JWT_DEMO.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeController {

    private final Utility utility;
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final SpringSecurity springSecurity;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder encode;

    @PostMapping("/authenticate/signUp")
    public ResponseEntity<?> registerUser( @RequestBody SignUpDTO signUpRequest) {
        if (userService.isAlreadyExistsUser(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new user's account
        User user =User.builder().username(signUpRequest.getUsername()).password(springSecurity.passwordEncoder().encode(signUpRequest.getPassword())).enabled(signUpRequest.isEnabled()).role(signUpRequest.getRole()).build();
        userService.saveUser(user);
       return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @GetMapping("/employees")
    public String getEmployees(){
        return "All employees ";
    }


    @PostMapping("/authenticate/signIn")
    public JWTResponse getAuthenticate(@RequestBody JWTRequest jwtRequest) throws Exception{
       try {
           Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                   jwtRequest.getUsername(),
                   jwtRequest.getPassword()
           ));
           if(authentication.isAuthenticated()){
               RefreshToken refreshToken = refreshTokenService.createRefreshToken(jwtRequest.getUsername());
               return JWTResponse.builder()
                       .jwtToken(utility.generateToken(jwtRequest.getUsername()))
                       .token(refreshToken.getToken())
                       .build();
           } else {
               throw new Exception("invalid user request..!!");
           }
       }catch (BadCredentialsException e){
           throw  new Exception("Bad Credential");
       }
    }


    @PostMapping("/authenticate/refreshToken")
    public JWTResponse refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = utility.generateToken(userInfo.getUsername());
                    return JWTResponse.builder()
                            .jwtToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
    }
}
