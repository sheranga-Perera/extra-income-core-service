package com.phx.ei.metadata.repository;

import com.phx.ei.metadata.entity.CompanySector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanySectorRepository extends JpaRepository<CompanySector, Long> {
    List<CompanySector> findByDeletedOrderByDisplayOrderAscNameAsc(Integer deleted);
}
