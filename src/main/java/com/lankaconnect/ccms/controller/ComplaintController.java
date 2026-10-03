package com.lankaconnect.ccms.controller;

import com.lankaconnect.ccms.model.Complaint;
import com.lankaconnect.ccms.service.ComplaintService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<?> submitComplaint(@RequestBody Complaint complaint, HttpServletRequest request) {
        try {
            Complaint saved = complaintService.submitComplaint(complaint, request.getRemoteAddr());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Complaint>> getCustomerComplaints(@PathVariable Long customerId) {
        return ResponseEntity.ok(complaintService.getComplaintsByCustomer(customerId));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<Complaint>> getStaffComplaints(@PathVariable Long staffId) {
        return ResponseEntity.ok(complaintService.getComplaintsByStaff(staffId));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Complaint>> getDepartmentComplaints(@PathVariable String department) {
        return ResponseEntity.ok(complaintService.getComplaintsByDepartment(department));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getComplaintByCode(@PathVariable String code) {
        return complaintService.getComplaintByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignComplaint(@PathVariable Long id, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            Long officerId = payload.get("officerId") != null ? Long.parseLong(payload.get("officerId").toString()) : null;
            String officerName = (String) payload.get("officerName");
            Long staffId = payload.get("staffId") != null ? Long.parseLong(payload.get("staffId").toString()) : null;
            String staffName = (String) payload.get("staffName");
            String department = (String) payload.get("department");

            Complaint updated = complaintService.assignComplaint(id, officerId, officerName, staffId, staffName, department, request.getRemoteAddr());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateInvestigationStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            String status = (String) payload.get("status");
            String resolutionNotes = (String) payload.get("resolutionNotes");
            Long staffId = payload.get("staffId") != null ? Long.parseLong(payload.get("staffId").toString()) : null;
            String staffName = (String) payload.get("staffName");

            Complaint updated = complaintService.updateInvestigationStatus(id, status, resolutionNotes, staffId, staffName, request.getRemoteAddr());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
