package com.lankaconnect.ccms.service;

import com.lankaconnect.ccms.model.ChatMessage;
import com.lankaconnect.ccms.model.ChatSession;
import com.lankaconnect.ccms.repository.ChatMessageRepository;
import com.lankaconnect.ccms.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatSessionRepository sessionRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private UserService userService;

    public ChatSession startCustomerSession(Long customerId, String customerName, String topic, String clientIp) {
        // Check for existing active or queued session
        Optional<ChatSession> existing = sessionRepository.findFirstByCustomerIdAndStatusIn(
                customerId, List.of("QUEUED", "ACTIVE")
        );
        if (existing.isPresent()) {
            return existing.get();
        }

        ChatSession session = new ChatSession();
        session.setCustomerId(customerId);
        session.setCustomerName(customerName);
        session.setTopic(topic != null && !topic.isEmpty() ? topic : "General Customer Inquiry");
        session.setStatus("QUEUED");
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        ChatSession saved = sessionRepository.save(session);

        // System greeting message
        ChatMessage systemMsg = new ChatMessage(
                saved.getId(), 0L, "LankaConnect Bot", "SYSTEM",
                "Hello " + customerName + "! Thank you for contacting LankaConnect Support. A Customer Service Officer will join shortly."
        );
        messageRepository.save(systemMsg);

        userService.logActivity(customerId, customerName, "CUSTOMER",
                "CHAT_START", "Customer initiated live chat session #" + saved.getId(), clientIp);

        return saved;
    }

    public ChatSession acceptChatSession(Long sessionId, Long agentId, String agentName, String clientIp) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        session.setAgentId(agentId);
        session.setAgentName(agentName);
        session.setStatus("ACTIVE");
        session.setUpdatedAt(LocalDateTime.now());

        ChatSession saved = sessionRepository.save(session);

        ChatMessage joinMsg = new ChatMessage(
                sessionId, agentId, agentName, "AGENT",
                "Officer " + agentName + " has joined the chat. How may I assist you today?"
        );
        messageRepository.save(joinMsg);

        userService.logActivity(agentId, agentName, "AGENT",
                "CHAT_ACCEPT", "Agent accepted chat session #" + sessionId, clientIp);

        return saved;
    }

    public ChatSession transferChatSession(Long sessionId, Long targetAgentId, String targetAgentName, Long currentAgentId, String currentAgentName, String clientIp) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        session.setAgentId(targetAgentId);
        session.setAgentName(targetAgentName);
        session.setStatus("TRANSFERRED");
        session.setUpdatedAt(LocalDateTime.now());

        ChatSession saved = sessionRepository.save(session);

        ChatMessage transferMsg = new ChatMessage(
                sessionId, 0L, "System", "SYSTEM",
                "Chat session was transferred from " + currentAgentName + " to Officer " + targetAgentName
        );
        messageRepository.save(transferMsg);

        userService.logActivity(currentAgentId, currentAgentName, "AGENT",
                "CHAT_TRANSFER", "Transferred chat session #" + sessionId + " to " + targetAgentName, clientIp);

        return saved;
    }

    public ChatMessage sendMessage(Long sessionId, Long senderId, String senderName, String senderRole, String messageText, String fileName, String fileUrl) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        ChatMessage msg = new ChatMessage(sessionId, senderId, senderName, senderRole, messageText);
        msg.setFileName(fileName);
        msg.setFileUrl(fileUrl);
        msg.setTimestamp(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    public List<ChatMessage> getMessages(Long sessionId) {
        return messageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    public List<ChatMessage> getNewMessages(Long sessionId, Long lastMessageId) {
        return messageRepository.findBySessionIdAndIdGreaterThanOrderByTimestampAsc(sessionId, lastMessageId);
    }

    public List<ChatSession> getQueuedSessions() {
        return sessionRepository.findByStatusOrderByCreatedAtDesc("QUEUED");
    }

    public List<ChatSession> getActiveSessions() {
        return sessionRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
    }

    public List<ChatSession> getSessionsByAgent(Long agentId) {
        return sessionRepository.findByAgentIdOrderByUpdatedAtDesc(agentId);
    }

    public List<ChatSession> getSessionsByCustomer(Long customerId) {
        return sessionRepository.findByCustomerIdOrderByUpdatedAtDesc(customerId);
    }

    public List<ChatSession> getAllSessions() {
        return sessionRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Optional<ChatSession> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    public ChatMessage updateChatMessage(Long messageId, String newMessage) {
        ChatMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Chat message not found"));
        msg.setMessage(newMessage);
        return messageRepository.save(msg);
    }

    public ChatSession updateChatSessionTopic(Long sessionId, String newTopic) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));
        session.setTopic(newTopic);
        session.setUpdatedAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public void deleteChatSession(Long sessionId) {
        messageRepository.deleteBySessionId(sessionId);
        sessionRepository.deleteById(sessionId);
    }

    public void deleteChatMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    public ChatSession closeSession(Long sessionId, Long userId, String username, String userRole, String clientIp) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        session.setStatus("CLOSED");
        session.setClosedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        ChatSession saved = sessionRepository.save(session);

        ChatMessage closeMsg = new ChatMessage(
                sessionId, 0L, "System", "SYSTEM",
                "Chat session has been closed. Thank you for choosing LankaConnect."
        );
        messageRepository.save(closeMsg);

        userService.logActivity(userId, username, userRole,
                "CHAT_CLOSE", "Closed chat session #" + sessionId, clientIp);

        return saved;
    }
}

