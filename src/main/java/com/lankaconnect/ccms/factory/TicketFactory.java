package com.lankaconnect.ccms.factory;

import com.lankaconnect.ccms.model.Ticket;
import java.time.LocalDateTime;

/**
 * Factory Method Pattern for creating Ticket objects.
 * Part of SLIIT SE2030 Software Engineering Design Patterns implementation.
 */
public class TicketFactory {

    /**
     * Creates a Ticket for a customer complaint.
     */
    public static Ticket createTicketForComplaint(Long complaintId, String complaintCode, Long customerId, String customerName, String title, String priority, String department, long ticketCount) {
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
        int slaDays = calculateSlaDays(ticket.getPriority());
        ticket.setDueDate(LocalDateTime.now().plusDays(slaDays));

        // Generate Ticket Code
        long codeNum = ticketCount + 1001;
        ticket.setTicketCode("TICK-2026-" + codeNum);

        return ticket;
    }

    /**
     * Creates a Standalone Ticket directly created by staff/system.
     */
    public static Ticket createStandaloneTicket(Ticket input, long ticketCount) {
        Ticket ticket = new Ticket();
        ticket.setTitle(input.getTitle());
        ticket.setCustomerName(input.getCustomerName());
        ticket.setComplaintCode(input.getComplaintCode());
        ticket.setAssignedDepartment(input.getAssignedDepartment() != null ? input.getAssignedDepartment() : "General Support");
        ticket.setAssignedStaffId(input.getAssignedStaffId());
        ticket.setAssignedStaffName(input.getAssignedStaffName());
        ticket.setPriority(input.getPriority() != null ? input.getPriority().toUpperCase() : "MEDIUM");
        ticket.setStatus(input.getStatus() != null ? input.getStatus().toUpperCase() : "OPEN");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        int slaDays = calculateSlaDays(ticket.getPriority());
        ticket.setDueDate(LocalDateTime.now().plusDays(slaDays));

        long codeNum = ticketCount + 1001;
        ticket.setTicketCode("TICK-2026-" + codeNum);

        return ticket;
    }

    private static int calculateSlaDays(String priority) {
        if (priority == null) return 4;
        return switch (priority.toUpperCase()) {
            case "CRITICAL" -> 1;
            case "HIGH" -> 2;
            case "MEDIUM" -> 4;
            default -> 7;
        };
    }
}
