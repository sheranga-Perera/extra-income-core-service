package com.phx.ei.jobs.repository;

import com.phx.ei.jobs.entity.JobPost;
import com.phx.ei.jobs.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    List<JobPost> findByStatusOrderByCreatedAtDesc(JobStatus status);

    @Query("""
        select j from JobPost j
        where j.status = :status
          and (
            lower(j.title) like lower(concat('%', :query, '%'))
            or lower(j.description) like lower(concat('%', :query, '%'))
            or lower(j.category) like lower(concat('%', :query, '%'))
            or lower(j.sector) like lower(concat('%', :query, '%'))
            or lower(j.location) like lower(concat('%', :query, '%'))
            or lower(j.companyName) like lower(concat('%', :query, '%'))
          )
        order by j.createdAt desc
        """)
    List<JobPost> searchByQuery(@Param("query") String query, @Param("status") JobStatus status);
}
