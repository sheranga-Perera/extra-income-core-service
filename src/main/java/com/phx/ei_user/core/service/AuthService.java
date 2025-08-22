package com.phx.ei_user.core.service;

import com.phx.ei_user.common.dto.request.LoginRequest;
import com.phx.ei_user.common.dto.request.RegisterRequest;
import com.phx.ei_user.common.entity.User;
import com.phx.ei_user.common.security.JwtUtils;
import com.phx.ei_user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request){
        User user = new User(UUID.randomUUID(), request.getUsername(),passwordEncoder.encode(request.getPassword()),"USER_ROLE");
        userRepository.save(user);
        return jwtUtils.generateToken(user.getUsername());
    }

    public String login(LoginRequest request){
       User user = userRepository.findByUsername(request.getUsername())
               .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

       if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
           throw new RuntimeException("Invalid Credentials");
       }

        return jwtUtils.generateToken(user.getUsername());
    }

}
