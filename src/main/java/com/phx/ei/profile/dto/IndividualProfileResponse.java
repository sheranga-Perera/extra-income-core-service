package com.phx.ei.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class IndividualProfileResponse {
    private UUID id;
    private String profilePicture;
    private String fullName;
    private String phone;
    private String location;
    private String bio;
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private String email;
    private String address;
    private String nicFront;
    private String nicBack;
    private Boolean hasDriversLicense;
    private String driversLicenseType;
    private String profession;
    private String preferredCategories;
    private String preferredSectors;
    private String skills;
}
