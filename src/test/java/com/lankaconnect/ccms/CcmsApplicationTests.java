package com.lankaconnect.ccms;

import com.lankaconnect.ccms.model.*;
import com.lankaconnect.ccms.repository.*;
import com.lankaconnect.ccms.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CcmsApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ReportingService reportingService;

    @Test
    void testFunction1_UserAccountManagementAndLoyaltyTiers() {
        // Customer 1: >12 months -> Gold
        User goldCust = new User("test_gold", "pass123", "Gold Member", "gold@test.com", "0771112233", "CUSTOMER", null, LocalDate.now().minusMonths(15));
        User savedGold = userService.registerUser(goldCust, "127.0.0.1");
        assertEquals("GOLD", savedGold.getLoyaltyTier(), "Loyalty tier for >12 months should be GOLD");

        // Customer 2: 7 months -> Silver
        User silverCust = new User("test_silver", "pass123", "Silver Member", "silver@test.com", "0772223344", "CUSTOMER", null, LocalDate.now().minusMonths(7));
        User savedSilver = userService.registerUser(silverCust, "127.0.0.1");
        assertEquals("SILVER", savedSilver.getLoyaltyTier(), "Loyalty tier for 6-12 months should be SILVER");

        // Staff / Admin should NOT have loyalty tier
        User staff = new User("test_staff_qa", "pass123", "Staff QA", "qa@test.com", "0773334455", "STAFF", "Technical Department", null);
        User savedStaff = userService.registerUser(staff, "127.0.0.1");
        assertNull(savedStaff.getLoyaltyTier(), "Staff must not have a loyalty tier badge");
    }

    @Test
    void testFunction2_ComplaintSubmissionAndRouting() {
        Complaint complaint = new Complaint();
        complaint.setTitle("Test Router Power Fault");
        complaint.setCategory("Technical");
        complaint.setRegion("Western");
        complaint.setPriority("CRITICAL");
        complaint.setDescription("Device does not power on");
        complaint.setCustomerId(1L);
        complaint.setCustomerName("Kasun Perera");

        Complaint saved = complaintService.submitComplaint(complaint, "127.0.0.1");
        assertNotNull(saved.getComplaintCode(), "Complaint code should be generated");
        assertEquals("SUBMITTED", saved.getStatus());
        assertEquals("Technical Department", saved.getAssignedDepartment());
    }

    @Test
    void testFunction3_LiveChatSupport() {
        ChatSession session = chatService.startCustomerSession(1L, "Kasun Perera", "Test Inquiry", "127.0.0.1");
        assertNotNull(session.getId());

        ChatMessage msg = chatService.sendMessage(session.getId(), 1L, "Kasun Perera", "CUSTOMER", "Hello support", null, null);
        assertNotNull(msg.getId());
        assertEquals("Hello support", msg.getMessage());

        List<ChatMessage> messages = chatService.getMessages(session.getId());
        assertTrue(messages.size() >= 2); // Initial system msg + customer msg
    }

    @Test
    void testFunction4_FeedbackAndCsatScore() {
        Feedback fb = new Feedback();
        fb.setTicketId(1L);
        fb.setTicketCode("TICK-2026-1001");
        fb.setComplaintId(1L);
        fb.setComplaintCode("CMP-2026-1001");
        fb.setCustomerId(1L);
        fb.setCustomerName("Kasun Perera");
        fb.setRating(5);
        fb.setCategory("Timeliness");
        fb.setComments("Excellent service!");

        Feedback saved = feedbackService.submitFeedback(fb, "127.0.0.1");
        assertNotNull(saved.getId());

        Map<String, Object> metrics = feedbackService.getCsatMetrics();
        assertTrue((Double) metrics.get("csatPercentage") >= 0);
    }

    @Test
    void testFunction5_TicketTrackingAndSla() {
        List<Ticket> tickets = ticketService.getAllTickets();
        assertFalse(tickets.isEmpty());
        Ticket first = tickets.get(0);
        assertNotNull(first.getDueDate(), "Ticket must have SLA due date");
    }

    @Test
    void testFunction6_ReportingAndRegionalSpikes() {
        Map<String, Object> stats = reportingService.getDashboardStatistics();
        assertTrue((Long) stats.get("totalComplaints") > 0);
        assertTrue((Long) stats.get("totalTickets") > 0);
    }
}
