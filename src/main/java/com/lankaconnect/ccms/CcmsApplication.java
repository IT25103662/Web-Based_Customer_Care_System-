package com.lankaconnect.ccms;

import com.lankaconnect.ccms.model.*;
import com.lankaconnect.ccms.repository.*;
import com.lankaconnect.ccms.service.ComplaintService;
import com.lankaconnect.ccms.service.FeedbackService;
import com.lankaconnect.ccms.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class CcmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcmsApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            UserService userService,
            ComplaintService complaintService,
            TicketRepository ticketRepository,
            FeedbackService feedbackService,
            AlertThresholdRepository thresholdRepository,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository
    ) {
        return args -> {
            if (userRepository.count() == 0) {
                System.out.println("Initializing LankaConnect CCMS sample dataset...");

                // 1. Users
                // Customers
                User cust1 = new User("customer1", "123456", "Kasun Perera", "kasun@gmail.com", "0771234567", "CUSTOMER", null, LocalDate.now().minusMonths(14)); // Gold (>12 months)
                userService.registerUser(cust1, "127.0.0.1");

                User cust2 = new User("customer2", "123456", "Nimali Silva", "nimali@gmail.com", "0719876543", "CUSTOMER", null, LocalDate.now().minusMonths(8)); // Silver (6-12 months)
                userService.registerUser(cust2, "127.0.0.1");

                User cust3 = new User("customer3", "123456", "Kamal Jayasinghe", "kamal@gmail.com", "0755551122", "CUSTOMER", null, LocalDate.now().minusMonths(2)); // Bronze (<6 months)
                userService.registerUser(cust3, "127.0.0.1");

                // Customer Service Officer / Live Chat Agent
                User agent1 = new User("agent_kamal", "123456", "Minsara D.M. (Live Chat Agent)", "agent.minsara@lankaconnect.lk", "0112345678", "AGENT", "Customer Support", null);
                userService.registerUser(agent1, "127.0.0.1");

                User agent2 = new User("officer_sarath", "123456", "Sarath Gunawardena (Support Officer)", "sarath.g@lankaconnect.lk", "0112345679", "AGENT", "Customer Support", null);
                userService.registerUser(agent2, "127.0.0.1");

                // Department Staff (Back-office / QA)
                User staffTech = new User("staff_tech", "123456", "Nuwan Pradeep (Tech Lead)", "nuwan.tech@lankaconnect.lk", "0778899001", "STAFF", "Technical Department", null);
                userService.registerUser(staffTech, "127.0.0.1");

                User staffBill = new User("staff_billing", "123456", "Sanduni Weerasinghe (Billing QA)", "sanduni.bill@lankaconnect.lk", "0778899002", "STAFF", "Billing Department", null);
                userService.registerUser(staffBill, "127.0.0.1");

                User staffNet = new User("staff_network", "123456", "Amal Senanayake (Network Engineer)", "amal.net@lankaconnect.lk", "0778899003", "STAFF", "Network Department", null);
                userService.registerUser(staffNet, "127.0.0.1");

                // Management (Customer Care Manager / Operations Manager)
                User manager = new User("manager", "123456", "Dissanayake R.D. (Operations Manager)", "manager.dissanayake@lankaconnect.lk", "0118877665", "MANAGER", "Executive Management", null);
                userService.registerUser(manager, "127.0.0.1");

                User ccManager = new User("cc_manager", "123456", "Denuwantha A.Y. (Customer Care Manager)", "denuwantha.ccm@lankaconnect.lk", "0118877666", "MANAGER", "Customer Care", null);
                userService.registerUser(ccManager, "127.0.0.1");

                // System Administrator
                User admin = new User("admin", "123456", "Fernando T.S.V. (System Admin)", "admin.fernando@lankaconnect.lk", "0110001122", "ADMIN", "IT Administration", null);
                userService.registerUser(admin, "127.0.0.1");

                // 2. Alert Thresholds for Regional Spikes
                thresholdRepository.save(new AlertThreshold("Western", 3, 2)); // >3 complaints in 2h triggers alert
                thresholdRepository.save(new AlertThreshold("Central", 2, 2));
                thresholdRepository.save(new AlertThreshold("Southern", 3, 2));
                thresholdRepository.save(new AlertThreshold("Northern", 2, 2));
                thresholdRepository.save(new AlertThreshold("Eastern", 2, 2));
                thresholdRepository.save(new AlertThreshold("North Western", 2, 2));

                // 3. Initial Complaints & Linked Tickets
                Complaint c1 = new Complaint();
                c1.setTitle("Fiber Internet Line Disconnected Unexpectedly");
                c1.setCategory("Network");
                c1.setDescription("Our home fiber broadband connection has been disconnected since this morning with Red LOS blinking.");
                c1.setRegion("Western");
                c1.setPriority("HIGH");
                c1.setCustomerId(cust1.getId());
                c1.setCustomerName(cust1.getFullName());
                c1.setCustomerEmail(cust1.getEmail());
                c1.setCustomerPhone(cust1.getPhone());
                Complaint savedC1 = complaintService.submitComplaint(c1, "127.0.0.1");

                // Assign c1 to Network Department staff
                complaintService.assignComplaint(savedC1.getId(), agent1.getId(), agent1.getFullName(), staffNet.getId(), staffNet.getFullName(), "Network Department", "127.0.0.1");

                Complaint c2 = new Complaint();
                c2.setTitle("Incorrect Overcharge on Monthly Invoice #9842");
                c2.setCategory("Billing");
                c2.setDescription("I was billed twice for the unlimited 100Mbps data add-on package in my August invoice.");
                c2.setRegion("Western");
                c2.setPriority("MEDIUM");
                c2.setCustomerId(cust2.getId());
                c2.setCustomerName(cust2.getFullName());
                c2.setCustomerEmail(cust2.getEmail());
                c2.setCustomerPhone(cust2.getPhone());
                Complaint savedC2 = complaintService.submitComplaint(c2, "127.0.0.1");
                complaintService.assignComplaint(savedC2.getId(), agent2.getId(), agent2.getFullName(), staffBill.getId(), staffBill.getFullName(), "Billing Department", "127.0.0.1");

                // c3 in Western to test Spike threshold
                Complaint c3 = new Complaint();
                c3.setTitle("Router Hardware Power LED Not Turning On");
                c3.setCategory("Technical");
                c3.setDescription("Power adapter is plugged in but the main router power LED does not light up. Replacement needed.");
                c3.setRegion("Western");
                c3.setPriority("CRITICAL");
                c3.setCustomerId(cust3.getId());
                c3.setCustomerName(cust3.getFullName());
                c3.setCustomerEmail(cust3.getEmail());
                c3.setCustomerPhone(cust3.getPhone());
                Complaint savedC3 = complaintService.submitComplaint(c3, "127.0.0.1");
                complaintService.assignComplaint(savedC3.getId(), agent1.getId(), agent1.getFullName(), staffTech.getId(), staffTech.getFullName(), "Technical Department", "127.0.0.1");

                // Mark c2 as Resolved and submit Feedback
                complaintService.updateInvestigationStatus(savedC2.getId(), "RESOLVED", "Audited invoice billing system. Erroneous charge of LKR 1,500 has been credited back to customer account.", staffBill.getId(), staffBill.getFullName(), "127.0.0.1");

                Ticket t2 = ticketRepository.findByComplaintId(savedC2.getId()).orElse(null);
                if (t2 != null) {
                    Feedback fb = new Feedback();
                    fb.setTicketId(t2.getId());
                    fb.setTicketCode(t2.getTicketCode());
                    fb.setComplaintId(savedC2.getId());
                    fb.setComplaintCode(savedC2.getComplaintCode());
                    fb.setCustomerId(cust2.getId());
                    fb.setCustomerName(cust2.getFullName());
                    fb.setRating(5);
                    fb.setCategory("Issue Resolution");
                    fb.setComments("Fast resolution! The refund appeared immediately on my portal.");
                    feedbackService.submitFeedback(fb, "127.0.0.1");
                }

                // 4. Sample Live Chat Session
                ChatSession chatSession = new ChatSession();
                chatSession.setCustomerId(cust1.getId());
                chatSession.setCustomerName(cust1.getFullName());
                chatSession.setAgentId(agent1.getId());
                chatSession.setAgentName(agent1.getFullName());
                chatSession.setStatus("ACTIVE");
                chatSession.setTopic("Fiber Connection Inquiry");
                chatSession.setCreatedAt(LocalDateTime.now().minusMinutes(15));
                chatSession.setUpdatedAt(LocalDateTime.now().minusMinutes(1));
                ChatSession savedChat = chatSessionRepository.save(chatSession);

                ChatMessage m1 = new ChatMessage(savedChat.getId(), cust1.getId(), cust1.getFullName(), "CUSTOMER", "Hi, my fiber broadband went offline suddenly. Can you check my connection status?");
                m1.setTimestamp(LocalDateTime.now().minusMinutes(14));
                chatMessageRepository.save(m1);

                ChatMessage m2 = new ChatMessage(savedChat.getId(), agent1.getId(), agent1.getFullName(), "AGENT", "Hello Mr. Kasun! Let me check the node telemetry for Colombo 07. Please give me 1 minute.");
                m2.setTimestamp(LocalDateTime.now().minusMinutes(12));
                chatMessageRepository.save(m2);

                ChatMessage m3 = new ChatMessage(savedChat.getId(), agent1.getId(), agent1.getFullName(), "AGENT", "I see a regional line maintenance ongoing. I have created high-priority ticket TICK-2026-1001 for our field team.");
                m3.setTimestamp(LocalDateTime.now().minusMinutes(8));
                chatMessageRepository.save(m3);

                ChatMessage m4 = new ChatMessage(savedChat.getId(), cust1.getId(), cust1.getFullName(), "CUSTOMER", "Thank you officer Minsara, that was very quick!");
                m4.setTimestamp(LocalDateTime.now().minusMinutes(2));
                chatMessageRepository.save(m4);

                System.out.println("LankaConnect CCMS initial dataset loaded successfully!");
            }
        };
    }
}
