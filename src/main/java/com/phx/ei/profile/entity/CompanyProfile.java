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

import java.util.UUID;

@Entity
@Table(name = "company_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {

    @Id
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String contactPerson;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    private String website;
}
