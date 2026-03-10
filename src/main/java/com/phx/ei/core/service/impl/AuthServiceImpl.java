package com.phx.ei.core.service.impl;

import com.phx.ei.common.dto.request.LoginRequest;
import com.phx.ei.common.dto.request.RegisterRequest;
import com.phx.ei.common.entity.User;
import com.phx.ei.common.security.JwtUtils;
import com.phx.ei.core.repository.UserRepository;
import com.phx.ei.core.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * 
     */
    @Override
    public String register(RegisterRequest request){
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getIdentifierType() == null || request.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing registration fields");
        }
        
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = new User(UUID.randomUUID(), request.getUsername(),
                passwordEncoder.encode(request.getPassword()), request.getIdentifierType(), request.getRole());
        userRepository.save(user);
        return jwtUtils.generateToken(user.getUsername());
    }

    /**
     * 
     */
    @Override
    public String login(LoginRequest request){
       User user = userRepository.findByUsername(request.getUsername())
               .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

       if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
           throw new RuntimeException("Invalid Credentials");
       }

        return jwtUtils.generateToken(user.getUsername());
    }

}
