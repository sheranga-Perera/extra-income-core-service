package com.phx.ei.jobs.entity;

import com.phx.ei.common.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = @UniqueConstraint(
                name = "job_applications_job_user_unique",
                columnNames = {"job_post_id", "individual_user_id"}
        )
)
@SQLDelete(sql = "UPDATE job_applications SET deleted = 1 WHERE id = ?")
@SQLRestriction("deleted = 0")
@Data
@NoArgsConstructor
public class JobApplication {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(optional = false)
    @JoinColumn(name = "individual_user_id", nullable = false)
    private User individualUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobApplicationStatus status = JobApplicationStatus.SUBMITTED;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String cvDocument;

    @Column(nullable = false)
    private Integer deleted = 0;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = JobApplicationStatus.SUBMITTED;
        }
        if (deleted == null) {
            deleted = 0;
        }
    }
}
