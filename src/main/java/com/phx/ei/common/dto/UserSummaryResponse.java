package com.phx.ei.common.dto;

import com.phx.ei.common.constant.IdentifierType;
import com.phx.ei.common.security.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSummaryResponse {
    private UUID id;
    private String username;
    private IdentifierType identifierType;
    private Role role;
}
