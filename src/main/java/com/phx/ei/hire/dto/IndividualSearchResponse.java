package com.phx.ei.hire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class IndividualSearchResponse {
    private UUID id;
    private String fullName;
    private String location;
    private String profession;
    private String skills;
    private String preferredCategories;
    private String preferredSectors;
    private String bio;
    private String phone;
    private String email;
}
