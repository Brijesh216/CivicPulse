package com.civicpulse.controller;

import com.civicpulse.dto.ComplaintRequest;
import com.civicpulse.dto.ComplaintUpdateRequest;
import com.civicpulse.dto.MessageResponse;
import com.civicpulse.entity.*;
import com.civicpulse.repository.*;
import com.civicpulse.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OfficerRepository officerRepository;

    @Autowired
    private ComplaintUpdateRepository complaintUpdateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> createComplaint(@RequestBody ComplaintRequest request) {
        try {
            System.out.println("📥 Received complaint creation request");
            System.out.println("   Title: " + request.getTitle());
            System.out.println("   Description: " + request.getDescription());
            System.out.println("   Area: " + request.getArea());
            System.out.println("   Department ID: " + request.getDepartmentId());
            System.out.println("   Priority: " + request.getPriority());
            System.out.println("   Latitude: " + request.getLatitude());
            System.out.println("   Longitude: " + request.getLongitude());
            
            User user = getCurrentUser();
            System.out.println("✓ User found: " + user.getName() + " (ID: " + user.getId() + ")");
            
            Complaint complaint = new Complaint();
            complaint.setTitle(request.getTitle());
            complaint.setDescription(request.getDescription());
            complaint.setArea(request.getArea());
            complaint.setPhotoUrl(request.getPhotoUrl());
            complaint.setPriority(request.getPriority());
            complaint.setLatitude(request.getLatitude());
            complaint.setLongitude(request.getLongitude());
            complaint.setCreatedBy(user);
            
            if (request.getDepartmentId() != null) {
                System.out.println("🔍 Looking up department: " + request.getDepartmentId());
                Department dept = departmentRepository.findById(request.getDepartmentId()).orElse(null);
                if (dept != null) {
                    complaint.setDepartment(dept);
                    System.out.println("✓ Department found: " + dept.getName());
                } else {
                    System.out.println("⚠️  Department not found with ID: " + request.getDepartmentId());
                }
            }

            System.out.println("💾 Saving complaint to database...");
            Complaint saved = complaintRepository.save(complaint);
            System.out.println("✅ Complaint created successfully! ID: " + saved.getId());
            
            // Notify all officers in the assigned department
            if (saved.getDepartment() != null) {
                System.out.println("🔔 Sending notifications to officers in " + saved.getDepartment().getName());
                List<Officer> officers = officerRepository.findByDepartmentId(saved.getDepartment().getId());
                for (Officer officer : officers) {
                    Notification notification = new Notification();
                    notification.setUser(officer.getUser());
                    notification.setMessage("New complaint filed: \"" + saved.getTitle() + "\" in " + saved.getDepartment().getName() + " (Priority: " + saved.getPriority() + ")");
                    notificationRepository.save(notification);
                    System.out.println("  📨 Notified officer: " + officer.getUser().getName());
                }
                System.out.println("✅ Notified " + officers.size() + " officers");
            }
            
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.out.println("❌ ERROR creating complaint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getComplaints() {
        User user = getCurrentUser();
        
        if ("ROLE_CITIZEN".equals(user.getRole())) {
            return ResponseEntity.ok(complaintRepository.findByCreatedByIdOrderByCreatedAtDesc(user.getId()));
        } else if ("ROLE_OFFICER".equals(user.getRole())) {
            Officer officer = officerRepository.findByUserId(user.getId()).orElse(null);
            if (officer != null) {
                // Return complaints assigned to this officer's department
                return ResponseEntity.ok(complaintRepository.findByDepartmentIdOrderByCreatedAtDesc(officer.getDepartment().getId()));
            }
            return ResponseEntity.ok(List.of());
        } else if ("ROLE_ADMIN".equals(user.getRole())) {
            return ResponseEntity.ok(complaintRepository.findAllByOrderByCreatedAtDesc());
        }
        
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaint(@PathVariable("id") Long id) {
        return ResponseEntity.ok(complaintRepository.findById(id).orElseThrow());
    }

    @PostMapping("/{id}/updates")
    public ResponseEntity<?> addUpdate(@PathVariable("id") Long id, @RequestBody ComplaintUpdateRequest request) {
        User user = getCurrentUser();
        Complaint complaint = complaintRepository.findById(id).orElseThrow();

        ComplaintUpdate update = new ComplaintUpdate();
        update.setComplaint(complaint);
        update.setUpdatedBy(user);
        update.setUpdateText(request.getUpdateText());
        update.setPhotoUrl(request.getPhotoUrl());

        complaintUpdateRepository.save(update);

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            complaint.setStatus(request.getStatus());
            if ("RESOLVED".equals(request.getStatus()) || "CLOSED".equals(request.getStatus())) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
            complaintRepository.save(complaint);

            // Notify citizen
            Notification notification = new Notification();
            notification.setUser(complaint.getCreatedBy());
            notification.setMessage("Status updated for your complaint: " + complaint.getTitle() + " -> " + request.getStatus());
            notificationRepository.save(notification);
        }

        return ResponseEntity.ok(new MessageResponse("Update added successfully!"));
    }

    @GetMapping("/{id}/updates")
    public ResponseEntity<List<ComplaintUpdate>> getUpdates(@PathVariable("id") Long id) {
        return ResponseEntity.ok(complaintUpdateRepository.findByComplaintIdOrderByCreatedAtAsc(id));
    }

    @GetMapping("/analytics/stats")
    public ResponseEntity<?> getAnalyticsStats() {
        try {
            System.out.println("📊 Fetching analytics statistics...");
            
            List<Complaint> allComplaints = complaintRepository.findAll();
            
            long total = allComplaints.size();
            long pending = allComplaints.stream().filter(c -> "PENDING".equals(c.getStatus())).count();
            long inProgress = allComplaints.stream().filter(c -> "IN_PROGRESS".equals(c.getStatus())).count();
            long resolved = allComplaints.stream().filter(c -> "RESOLVED".equals(c.getStatus())).count();
            
            System.out.println("✅ Analytics computed: Total=" + total + ", Pending=" + pending + 
                             ", InProgress=" + inProgress + ", Resolved=" + resolved);
            
            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                put("total", total);
                put("pending", pending);
                put("inProgress", inProgress);
                put("resolved", resolved);
            }});
        } catch (Exception e) {
            System.err.println("❌ Analytics error: " + e.getMessage());
            return ResponseEntity.status(500).body(new MessageResponse("Error fetching analytics"));
        }
    }
}
