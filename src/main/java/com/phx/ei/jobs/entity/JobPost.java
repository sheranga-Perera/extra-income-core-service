package com.phx.ei.jobs.entity;

import com.phx.ei.common.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_posts")
@Data
@NoArgsConstructor
public class JobPost {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_user_id", nullable = false)
    private User companyUser;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private String category;

    private String sector;

    private String location;

    private Integer hoursPerWeek;

    private BigDecimal hourlyRate;

    private String contractType;

    private String contractDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
