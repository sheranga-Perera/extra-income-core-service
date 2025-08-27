package com.phx.ei.common.dto.request;


import com.phx.ei.common.constant.IdentifierType;
import com.phx.ei.common.security.Role;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private IdentifierType identifierType;
    private Role role;
}
