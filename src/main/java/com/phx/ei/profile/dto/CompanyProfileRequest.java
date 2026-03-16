package com.phx.ei.profile.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompanyProfileRequest {
    private String companyName;
    private String registrationNumber;
    private String contactPerson;
    private String contactEmail;
    private String phone;
    private String address;
    private String website;
    private String bio;
    private String sector;
    private List<String> legalDocs;
}
