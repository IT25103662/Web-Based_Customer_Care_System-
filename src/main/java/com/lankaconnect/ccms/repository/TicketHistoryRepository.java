package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
    List<TicketHistory> findByTicketIdOrderByTimestampDesc(Long ticketId);
    List<TicketHistory> findByTicketCodeOrderByTimestampDesc(String ticketCode);
}
