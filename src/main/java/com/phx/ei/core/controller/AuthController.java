package com.phx.ei.core.controller;

import com.phx.ei.common.dto.request.AuthResponse;
import com.phx.ei.common.dto.request.LoginRequest;
import com.phx.ei.common.dto.request.RegisterRequest;
import com.phx.ei.core.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(value = "http://localhost:4200")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * This API will handle user registrations
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("Auth register request received: username={}, identifierType={}, role={}",
                request.getUsername(), request.getIdentifierType(), request.getRole());
        String token = authService.register(request);
        log.info("Auth register succeeded: username={}", request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * This API handles user login
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Auth login request received: username={}", request.getUsername());
        String token = authService.login(request);
        log.info("Auth login succeeded: username={}", request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
