package com.lankaconnect.ccms.controller;

import com.lankaconnect.ccms.model.Ticket;
import com.lankaconnect.ccms.model.TicketHistory;
import com.lankaconnect.ccms.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Ticket>> getCustomerTickets(@PathVariable Long customerId) {
        return ResponseEntity.ok(ticketService.getTicketsByCustomer(customerId));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<Ticket>> getStaffTickets(@PathVariable Long staffId) {
        return ResponseEntity.ok(ticketService.getTicketsByStaff(staffId));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Ticket>> getDepartmentTickets(@PathVariable String department) {
        return ResponseEntity.ok(ticketService.getTicketsByDepartment(department));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getTicketByCode(@PathVariable String code) {
        return ticketService.getTicketByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TicketHistory>> getTicketHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketHistory(id));
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket, @RequestParam(required = false, defaultValue = "Staff") String creatorName, @RequestParam(required = false, defaultValue = "STAFF") String userRole) {
        try {
            Ticket created = ticketService.createStandaloneTicket(ticket, creatorName, userRole);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ticket>> searchTickets(@RequestParam(required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(ticketService.searchTickets(query));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket, @RequestParam(required = false, defaultValue = "Staff") String changedBy, @RequestParam(required = false, defaultValue = "STAFF") String userRole) {
        try {
            Ticket updated = ticketService.updateTicket(id, ticket, changedBy, userRole);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id, @RequestParam(required = false, defaultValue = "Staff") String changedBy, @RequestParam(required = false, defaultValue = "STAFF") String userRole) {
        try {
            ticketService.deleteTicket(id, changedBy, userRole);
            return ResponseEntity.ok(Map.of("success", true, "message", "Ticket deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            String changedBy = payload.get("changedBy");
            String userRole = payload.get("userRole");
            String remarks = payload.get("remarks");

            Ticket updated = ticketService.updateTicketStatus(id, status, changedBy, userRole, remarks);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

