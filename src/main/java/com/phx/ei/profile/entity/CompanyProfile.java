package com.phx.ei.profile.entity;

import com.phx.ei.common.entity.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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

    @Column(length = 1000)
    private String bio;

    private String sector;

    @ElementCollection
    @CollectionTable(name = "company_legal_docs", joinColumns = @JoinColumn(name = "company_profile_id"))
    @Column(name = "document", columnDefinition = "TEXT")
    private List<String> legalDocs;
}
