package com.lankaconnect.ccms.service;

import com.lankaconnect.ccms.model.Ticket;
import com.lankaconnect.ccms.model.TicketHistory;
import com.lankaconnect.ccms.repository.TicketHistoryRepository;
import com.lankaconnect.ccms.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    public Ticket createTicketForComplaint(Long complaintId, String complaintCode, Long customerId, String customerName, String title, String priority, String department) {
        Ticket ticket = new Ticket();
        ticket.setComplaintId(complaintId);
        ticket.setComplaintCode(complaintCode);
        ticket.setCustomerId(customerId);
        ticket.setCustomerName(customerName);
        ticket.setTitle(title);
        ticket.setPriority(priority != null ? priority.toUpperCase() : "MEDIUM");
        ticket.setStatus("OPEN");
        ticket.setAssignedDepartment(department != null ? department : "General Support");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        // SLA Due Date calculation based on Priority
        int slaDays = switch (ticket.getPriority()) {
            case "CRITICAL" -> 1;
            case "HIGH" -> 2;
            case "MEDIUM" -> 4;
            default -> 7;
        };
        ticket.setDueDate(LocalDateTime.now().plusDays(slaDays));

        // Generate Ticket Code
        long count = ticketRepository.count() + 1001;
        ticket.setTicketCode("TICK-2026-" + count);

        Ticket saved = ticketRepository.save(ticket);

        // Record Initial History Audit
        logTicketHistory(saved.getId(), saved.getTicketCode(), "System", "SYSTEM",
                null, "OPEN", "Ticket automatically generated from Complaint " + complaintCode);

        return saved;
    }

    public Ticket updateTicketStatus(Long ticketId, String newStatus, String changedBy, String userRole, String remarks) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        String prevStatus = ticket.getStatus();
        ticket.setStatus(newStatus.toUpperCase());
        ticket.setUpdatedAt(LocalDateTime.now());

        if ("RESOLVED".equalsIgnoreCase(newStatus) || "CLOSED".equalsIgnoreCase(newStatus)) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        Ticket saved = ticketRepository.save(ticket);

        // Record History Audit
        logTicketHistory(ticket.getId(), ticket.getTicketCode(), changedBy, userRole, prevStatus, newStatus, remarks);

        return saved;
    }

    public Ticket assignTicketStaff(Long ticketId, Long staffId, String staffName, String department, String changedBy, String userRole) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        ticket.setAssignedStaffId(staffId);
        ticket.setAssignedStaffName(staffName);
        if (department != null && !department.isEmpty()) {
            ticket.setAssignedDepartment(department);
        }
        ticket.setStatus("IN_PROGRESS");
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);

        logTicketHistory(ticket.getId(), ticket.getTicketCode(), changedBy, userRole,
                ticket.getStatus(), "IN_PROGRESS", "Assigned to staff: " + staffName + " (" + department + ")");

        return saved;
    }

    public void logTicketHistory(Long ticketId, String ticketCode, String changedBy, String userRole, String prevStatus, String newStatus, String remarks) {
        TicketHistory history = new TicketHistory(ticketId, ticketCode, changedBy, userRole, prevStatus, newStatus, remarks);
        ticketHistoryRepository.save(history);
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> list = ticketRepository.findAllByOrderByCreatedAtDesc();
        // Check overdue
        LocalDateTime now = LocalDateTime.now();
        for (Ticket t : list) {
            if (!"RESOLVED".equalsIgnoreCase(t.getStatus()) && !"CLOSED".equalsIgnoreCase(t.getStatus())) {
                if (t.getDueDate() != null && t.getDueDate().isBefore(now)) {
                    t.setOverdue(true);
                }
            }
        }
        return list;
    }

    public List<Ticket> getTicketsByCustomer(Long customerId) {
        return ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Ticket> getTicketsByStaff(Long staffId) {
        return ticketRepository.findByAssignedStaffIdOrderByCreatedAtDesc(staffId);
    }

    public List<Ticket> getTicketsByDepartment(String department) {
        return ticketRepository.findByAssignedDepartmentOrderByCreatedAtDesc(department);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Optional<Ticket> getTicketByCode(String code) {
        return ticketRepository.findByTicketCode(code);
    }

    public List<TicketHistory> getTicketHistory(Long ticketId) {
        return ticketHistoryRepository.findByTicketIdOrderByTimestampDesc(ticketId);
    }
}
