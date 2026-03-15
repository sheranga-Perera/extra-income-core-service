package com.phx.ei.core.controller;

import com.phx.ei.common.dto.UserSummaryResponse;
import com.phx.ei.common.entity.User;
import com.phx.ei.core.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getCurrentUser() {
        log.info("User summary request received: /users/me");
        User user = currentUserService.getCurrentUser();
        log.info("User summary resolved: userId={}, username={}, role={}",
                user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getIdentifierType(),
                user.getRole()
        ));
    }
}
