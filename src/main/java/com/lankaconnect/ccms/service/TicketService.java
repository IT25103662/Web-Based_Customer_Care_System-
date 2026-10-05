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
        long currentCount = ticketRepository.count();
        Ticket ticket = com.lankaconnect.ccms.factory.TicketFactory.createTicketForComplaint(
                complaintId, complaintCode, customerId, customerName, title, priority, department, currentCount
        );

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

    public Ticket createStandaloneTicket(Ticket ticket, String creatorName, String userRole) {
        long currentCount = ticketRepository.count();
        Ticket createdTicket = com.lankaconnect.ccms.factory.TicketFactory.createStandaloneTicket(ticket, currentCount);

        Ticket saved = ticketRepository.save(createdTicket);
        logTicketHistory(saved.getId(), saved.getTicketCode(), creatorName != null ? creatorName : "Staff",
                userRole != null ? userRole : "STAFF", null, saved.getStatus(), "Manually raised ticket");

        return saved;
    }


    public Ticket updateTicket(Long id, Ticket details, String changedBy, String userRole) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        String prevStatus = ticket.getStatus();
        if (details.getTitle() != null && !details.getTitle().trim().isEmpty()) {
            ticket.setTitle(details.getTitle());
        }
        if (details.getPriority() != null && !details.getPriority().trim().isEmpty()) {
            ticket.setPriority(details.getPriority().toUpperCase());
        }
        if (details.getStatus() != null && !details.getStatus().trim().isEmpty()) {
            ticket.setStatus(details.getStatus().toUpperCase());
        }
        if (details.getAssignedDepartment() != null && !details.getAssignedDepartment().trim().isEmpty()) {
            ticket.setAssignedDepartment(details.getAssignedDepartment());
        }
        if (details.getAssignedStaffId() != null) {
            ticket.setAssignedStaffId(details.getAssignedStaffId());
        }
        if (details.getAssignedStaffName() != null) {
            ticket.setAssignedStaffName(details.getAssignedStaffName());
        }
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);
        logTicketHistory(saved.getId(), saved.getTicketCode(), changedBy != null ? changedBy : "Staff",
                userRole != null ? userRole : "STAFF", prevStatus, saved.getStatus(), "Updated ticket details");

        return saved;
    }

    public void deleteTicket(Long id, String changedBy, String userRole) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticketHistoryRepository.deleteByTicketId(id);
        ticketRepository.deleteById(id);
    }

    public List<Ticket> searchTickets(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTickets();
        }
        List<Ticket> list = ticketRepository.searchTickets(query.trim());
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

    public List<TicketHistory> getTicketHistory(Long ticketId) {
        return ticketHistoryRepository.findByTicketIdOrderByTimestampDesc(ticketId);
    }
}

