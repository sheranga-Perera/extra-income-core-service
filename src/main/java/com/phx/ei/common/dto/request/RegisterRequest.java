package com.phx.ei.common.dto.request;


import com.phx.ei.common.constant.IdentifierType;
import com.phx.ei.common.security.Role;
import java.util.List;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private IdentifierType identifierType;
    private Role role;

    // Individual registration fields
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String nicFront;
    private String nicBack;
    private Boolean hasDriversLicense;
    private String driversLicenseType;
    private String profession;
    private String preferredCategories;
    private String preferredSectors;
    private String skills;

    // Company registration fields
    private String companyName;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String bio;
    private String sector;
    private List<String> legalDocs;
}
