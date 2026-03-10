package com.phx.ei.core.controller;

import com.phx.ei.common.dto.UserSummaryResponse;
import com.phx.ei.common.entity.User;
import com.phx.ei.core.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getIdentifierType(),
                user.getRole()
        ));
    }
}
