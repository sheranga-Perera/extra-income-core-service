package com.phx.ei.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class IndividualProfileResponse {
    private UUID id;
    private String fullName;
    private String phone;
    private String location;
    private String bio;
}
