package com.phx.ei.profile.dto;

import lombok.Data;

@Data
public class CompanyProfileRequest {
    private String companyName;
    private String registrationNumber;
    private String contactPerson;
    private String contactEmail;
    private String phone;
    private String address;
    private String website;
}
