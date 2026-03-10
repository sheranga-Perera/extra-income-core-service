package com.phx.ei.profile.dto;

import lombok.Data;

@Data
public class IndividualProfileRequest {
    private String fullName;
    private String phone;
    private String location;
    private String bio;
}
