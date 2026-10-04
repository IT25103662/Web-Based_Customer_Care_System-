package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintCode(String complaintCode);
    List<Complaint> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Complaint> findByAssignedStaffIdOrderByCreatedAtDesc(Long staffId);
    List<Complaint> findByAssignedDepartmentOrderByCreatedAtDesc(String department);
    List<Complaint> findByStatusOrderByCreatedAtDesc(String status);
    List<Complaint> findAllByOrderByCreatedAtDesc();

    // Query for Regional threshold calculation
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.region = :region AND c.createdAt >= :since")
    long countByRegionSince(@Param("region") String region, @Param("since") LocalDateTime since);

    @Query("SELECT c.region, COUNT(c) FROM Complaint c GROUP BY c.region")
    List<Object[]> countComplaintsByRegion();

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> countComplaintsByCategory();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countComplaintsByStatus();

    @Query("SELECT c FROM Complaint c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate ORDER BY c.createdAt DESC")
    List<Complaint> findComplaintsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
