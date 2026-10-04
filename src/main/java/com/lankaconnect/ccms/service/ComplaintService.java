package com.lankaconnect.ccms.service;

import com.lankaconnect.ccms.model.Complaint;
import com.lankaconnect.ccms.model.Ticket;
import com.lankaconnect.ccms.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    public Complaint submitComplaint(Complaint complaint, String clientIp) {
        long count = complaintRepository.count() + 1001;
        complaint.setComplaintCode("CMP-2026-" + count);
        complaint.setStatus("SUBMITTED");
        complaint.setCreatedAt(LocalDateTime.now());

        // Automatic Department Routing based on category
        if (complaint.getAssignedDepartment() == null || complaint.getAssignedDepartment().isEmpty()) {
            String category = complaint.getCategory() != null ? complaint.getCategory().toLowerCase() : "";
            if (category.contains("tech") || category.contains("hardware") || category.contains("software")) {
                complaint.setAssignedDepartment("Technical Department");
            } else if (category.contains("bill") || category.contains("payment") || category.contains("refund")) {
                complaint.setAssignedDepartment("Billing Department");
            } else if (category.contains("net") || category.contains("fiber") || category.contains("signal")) {
                complaint.setAssignedDepartment("Network Department");
            } else {
                complaint.setAssignedDepartment("Customer Operations");
            }
        }

        Complaint savedComplaint = complaintRepository.save(complaint);

        // Auto create corresponding Ticket
        Ticket ticket = ticketService.createTicketForComplaint(
                savedComplaint.getId(),
                savedComplaint.getComplaintCode(),
                savedComplaint.getCustomerId(),
                savedComplaint.getCustomerName(),
                savedComplaint.getTitle(),
                savedComplaint.getPriority(),
                savedComplaint.getAssignedDepartment()
        );

        userService.logActivity(savedComplaint.getCustomerId(), savedComplaint.getCustomerName(), "CUSTOMER",
                "SUBMIT_COMPLAINT", "Submitted complaint " + savedComplaint.getComplaintCode() + " -> Ticket " + ticket.getTicketCode(), clientIp);

        return savedComplaint;
    }

    public Complaint assignComplaint(Long complaintId, Long officerId, String officerName, Long staffId, String staffName, String department, String clientIp) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        if (officerId != null) {
            complaint.setAssignedOfficerId(officerId);
            complaint.setAssignedOfficerName(officerName);
        }
        if (staffId != null) {
            complaint.setAssignedStaffId(staffId);
            complaint.setAssignedStaffName(staffName);
        }
        if (department != null && !department.isEmpty()) {
            complaint.setAssignedDepartment(department);
        }

        complaint.setStatus("ASSIGNED");
        Complaint saved = complaintRepository.save(complaint);

        // Update ticket assignment
        Optional<Ticket> ticketOpt = ticketService.getAllTickets().stream()
                .filter(t -> complaint.getId().equals(t.getComplaintId()))
                .findFirst();

        ticketOpt.ifPresent(ticket -> ticketService.assignTicketStaff(
                ticket.getId(), staffId, staffName, department, officerName, "AGENT"
        ));

        userService.logActivity(officerId, officerName, "AGENT",
                "ASSIGN_COMPLAINT", "Assigned complaint " + complaint.getComplaintCode() + " to " + staffName + " (" + department + ")", clientIp);

        return saved;
    }

    public Complaint updateInvestigationStatus(Long complaintId, String status, String resolutionNotes, Long staffId, String staffName, String clientIp) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        complaint.setStatus(status.toUpperCase());
        if (resolutionNotes != null && !resolutionNotes.isEmpty()) {
            complaint.setResolutionNotes(resolutionNotes);
        }
        if ("RESOLVED".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(status)) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint saved = complaintRepository.save(complaint);

        // Sync Ticket Status
        Optional<Ticket> ticketOpt = ticketService.getAllTickets().stream()
                .filter(t -> complaint.getId().equals(t.getComplaintId()))
                .findFirst();

        ticketOpt.ifPresent(ticket -> ticketService.updateTicketStatus(
                ticket.getId(), status, staffName, "STAFF", resolutionNotes
        ));

        userService.logActivity(staffId, staffName, "STAFF",
                "UPDATE_COMPLAINT", "Updated complaint " + complaint.getComplaintCode() + " status to " + status, clientIp);

        return saved;
    }

    public Complaint updateComplaint(Long id, Complaint details, String clientIp) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        if (details.getTitle() != null && !details.getTitle().trim().isEmpty()) {
            complaint.setTitle(details.getTitle());
        }
        if (details.getCategory() != null && !details.getCategory().trim().isEmpty()) {
            complaint.setCategory(details.getCategory());
        }
        if (details.getDescription() != null && !details.getDescription().trim().isEmpty()) {
            complaint.setDescription(details.getDescription());
        }
        if (details.getRegion() != null && !details.getRegion().trim().isEmpty()) {
            complaint.setRegion(details.getRegion());
        }
        if (details.getPriority() != null && !details.getPriority().trim().isEmpty()) {
            complaint.setPriority(details.getPriority().toUpperCase());
        }
        if (details.getAssignedDepartment() != null && !details.getAssignedDepartment().trim().isEmpty()) {
            complaint.setAssignedDepartment(details.getAssignedDepartment());
        }
        if (details.getStatus() != null && !details.getStatus().trim().isEmpty()) {
            complaint.setStatus(details.getStatus().toUpperCase());
        }
        if (details.getResolutionNotes() != null) {
            complaint.setResolutionNotes(details.getResolutionNotes());
        }

        Complaint saved = complaintRepository.save(complaint);
        userService.logActivity(saved.getCustomerId(), saved.getCustomerName(), "USER",
                "UPDATE_COMPLAINT_DETAILS", "Updated complaint " + saved.getComplaintCode(), clientIp);
        return saved;
    }

    public void deleteComplaint(Long id, String clientIp) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        complaintRepository.deleteById(id);
        userService.logActivity(complaint.getCustomerId(), complaint.getCustomerName(), "USER",
                "DELETE_COMPLAINT", "Deleted complaint " + complaint.getComplaintCode(), clientIp);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Complaint> getComplaintsByCustomer(Long customerId) {
        return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Complaint> getComplaintsByStaff(Long staffId) {
        return complaintRepository.findByAssignedStaffIdOrderByCreatedAtDesc(staffId);
    }

    public List<Complaint> getComplaintsByDepartment(String department) {
        return complaintRepository.findByAssignedDepartmentOrderByCreatedAtDesc(department);
    }

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public Optional<Complaint> getComplaintByCode(String code) {
        return complaintRepository.findByComplaintCode(code);
    }
}

