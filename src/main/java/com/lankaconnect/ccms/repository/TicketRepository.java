package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketCode(String ticketCode);
    Optional<Ticket> findByComplaintId(Long complaintId);
    List<Ticket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Ticket> findByAssignedStaffIdOrderByCreatedAtDesc(Long staffId);
    List<Ticket> findByAssignedDepartmentOrderByCreatedAtDesc(String department);
    List<Ticket> findByStatusOrderByCreatedAtDesc(String status);
    List<Ticket> findAllByOrderByCreatedAtDesc();

    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> countTicketsByStatus();

    @Query("SELECT t.priority, COUNT(t) FROM Ticket t GROUP BY t.priority")
    List<Object[]> countTicketsByPriority();

    long countByOverdueTrue();
}
