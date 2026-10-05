package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByCustomerIdOrderByUpdatedAtDesc(Long customerId);
    List<ChatSession> findByAgentIdOrderByUpdatedAtDesc(Long agentId);
    List<ChatSession> findByStatusOrderByCreatedAtDesc(String status);
    List<ChatSession> findAllByOrderByUpdatedAtDesc();
    Optional<ChatSession> findFirstByCustomerIdAndStatusIn(Long customerId, List<String> statuses);
}
