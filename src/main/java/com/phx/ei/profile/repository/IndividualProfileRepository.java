package com.phx.ei.profile.repository;

import com.phx.ei.profile.entity.IndividualProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndividualProfileRepository extends JpaRepository<IndividualProfile, UUID> {
    Optional<IndividualProfile> findByUserId(UUID userId);

    @Query("""
            SELECT p FROM IndividualProfile p
            WHERE (:location IS NULL OR lower(coalesce(p.location, '')) LIKE lower(concat('%', :location, '%')))
              AND (:profession IS NULL OR lower(coalesce(p.profession, '')) LIKE lower(concat('%', :profession, '%')))
              AND (:skills IS NULL OR lower(coalesce(p.skills, '')) LIKE lower(concat('%', :skills, '%')))
            """)
    List<IndividualProfile> searchIndividuals(
            @Param("location") String location,
            @Param("profession") String profession,
            @Param("skills") String skills
    );
}
