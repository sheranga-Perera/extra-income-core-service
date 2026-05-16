package com.phx.ei.profile.entity;

import com.phx.ei.common.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "individual_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualProfile {

    @Id
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String profilePicture;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String location;

    @Column(length = 1000)
    private String bio;

    private String firstName;

    private String lastName;

    private LocalDate dob;

    private String gender;

    private String email;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String nicFront;

    @Column(columnDefinition = "TEXT")
    private String nicBack;

    @Column(nullable = false)
    private boolean hasDriversLicense;

    private String driversLicenseType;

    private String profession;

    private String preferredCategories;

    private String preferredSectors;

    @Column(length = 1000)
    private String skills;
}
