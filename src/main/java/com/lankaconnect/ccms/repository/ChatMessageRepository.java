package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionIdOrderByTimestampAsc(Long sessionId);
    List<ChatMessage> findBySessionIdAndIdGreaterThanOrderByTimestampAsc(Long sessionId, Long lastId);
}
