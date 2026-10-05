package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.AlertThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertThresholdRepository extends JpaRepository<AlertThreshold, Long> {
    Optional<AlertThreshold> findByRegion(String region);
    List<AlertThreshold> findByActiveTrue();
}
