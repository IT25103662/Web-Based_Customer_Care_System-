package com.lankaconnect.ccms.controller;

import com.lankaconnect.ccms.model.ChatMessage;
import com.lankaconnect.ccms.model.ChatSession;
import com.lankaconnect.ccms.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/start")
    public ResponseEntity<?> startCustomerChat(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            Long customerId = Long.parseLong(payload.get("customerId").toString());
            String customerName = (String) payload.get("customerName");
            String topic = (String) payload.get("topic");

            ChatSession session = chatService.startCustomerSession(customerId, customerName, topic, request.getRemoteAddr());
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/accept/{sessionId}")
    public ResponseEntity<?> acceptChat(@PathVariable Long sessionId, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            Long agentId = Long.parseLong(payload.get("agentId").toString());
            String agentName = (String) payload.get("agentName");

            ChatSession session = chatService.acceptChatSession(sessionId, agentId, agentName, request.getRemoteAddr());
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/transfer/{sessionId}")
    public ResponseEntity<?> transferChat(@PathVariable Long sessionId, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            Long targetAgentId = Long.parseLong(payload.get("targetAgentId").toString());
            String targetAgentName = (String) payload.get("targetAgentName");
            Long currentAgentId = Long.parseLong(payload.get("currentAgentId").toString());
            String currentAgentName = (String) payload.get("currentAgentName");

            ChatSession session = chatService.transferChatSession(sessionId, targetAgentId, targetAgentName, currentAgentId, currentAgentName, request.getRemoteAddr());
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> payload) {
        try {
            Long sessionId = Long.parseLong(payload.get("sessionId").toString());
            Long senderId = Long.parseLong(payload.get("senderId").toString());
            String senderName = (String) payload.get("senderName");
            String senderRole = (String) payload.get("senderRole");
            String message = (String) payload.get("message");
            String fileName = (String) payload.get("fileName");
            String fileUrl = (String) payload.get("fileUrl");

            ChatMessage msg = chatService.sendMessage(sessionId, senderId, senderName, senderRole, message, fileName, fileUrl);
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/messages/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getMessages(sessionId));
    }

    @GetMapping("/messages/{sessionId}/poll")
    public ResponseEntity<List<ChatMessage>> pollMessages(@PathVariable Long sessionId, @RequestParam(defaultValue = "0") Long lastId) {
        return ResponseEntity.ok(chatService.getNewMessages(sessionId, lastId));
    }

    @GetMapping("/queue")
    public ResponseEntity<List<ChatSession>> getQueue() {
        return ResponseEntity.ok(chatService.getQueuedSessions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ChatSession>> getActiveSessions() {
        return ResponseEntity.ok(chatService.getActiveSessions());
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<ChatSession>> getAgentSessions(@PathVariable Long agentId) {
        return ResponseEntity.ok(chatService.getSessionsByAgent(agentId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ChatSession>> getCustomerSessions(@PathVariable Long customerId) {
        return ResponseEntity.ok(chatService.getSessionsByCustomer(customerId));
    }

    @PostMapping("/close/{sessionId}")
    public ResponseEntity<?> closeChat(@PathVariable Long sessionId, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(payload.get("userId").toString());
            String username = (String) payload.get("username");
            String userRole = (String) payload.get("userRole");

            ChatSession session = chatService.closeSession(sessionId, userId, username, userRole, request.getRemoteAddr());
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
