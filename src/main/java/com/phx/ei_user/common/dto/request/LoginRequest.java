package com.phx.ei_user.common.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
