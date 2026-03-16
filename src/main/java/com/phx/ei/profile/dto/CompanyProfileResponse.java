package com.phx.ei.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CompanyProfileResponse {
    private UUID id;
    private String companyName;
    private String registrationNumber;
    private String contactPerson;
    private String contactEmail;
    private String phone;
    private String address;
    private String website;
}
