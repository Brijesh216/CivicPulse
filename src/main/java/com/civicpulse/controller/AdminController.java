package com.civicpulse.controller;

import com.civicpulse.dto.MessageResponse;
import com.civicpulse.entity.Complaint;
import com.civicpulse.entity.Department;
import com.civicpulse.entity.Notification;
import com.civicpulse.entity.Officer;
import com.civicpulse.entity.User;
import com.civicpulse.repository.ComplaintRepository;
import com.civicpulse.repository.DepartmentRepository;
import com.civicpulse.repository.NotificationRepository;
import com.civicpulse.repository.OfficerRepository;
import com.civicpulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private OfficerRepository officerRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/officers")
    public ResponseEntity<List<Officer>> getAllOfficers() {
        return ResponseEntity.ok(officerRepository.findAll());
    }

    @PostMapping("/officers/create")
    public ResponseEntity<?> createOfficer(@RequestParam("userId") Long userId, @RequestParam("departmentId") Long departmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));
        
        // Check if user is already an officer
        Optional<Officer> existingOfficer = officerRepository.findByUserId(userId);
        if (existingOfficer.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: User is already an officer."));
        }

        // Update user role to officer if not already
        user.setRole("ROLE_OFFICER");
        userRepository.save(user);

        // Create officer record
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Error: Department is not found."));
        
        Officer officer = new Officer();
        officer.setUser(user);
        officer.setDepartment(department);
        officerRepository.save(officer);

        return ResponseEntity.ok(new MessageResponse("Officer created successfully!"));
    }

    @PostMapping("/officers/create-with-credentials")
    public ResponseEntity<?> createOfficerWithCredentials(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("departmentId") Long departmentId) {
        
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use."));
        }

        // Create user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_OFFICER");
        userRepository.save(user);

        // Create officer record
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Error: Department is not found."));
        
        Officer officer = new Officer();
        officer.setUser(user);
        officer.setDepartment(department);
        officerRepository.save(officer);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Officer created successfully!");
        response.put("officerId", officer.getId());
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("department", department.getName());
        
        return ResponseEntity.ok(response);
    }
    public ResponseEntity<?> assignComplaint(@PathVariable("id") Long id, @RequestParam("officerId") Long officerId) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow();
        Officer officer = officerRepository.findById(officerId).orElseThrow();

        complaint.setAssignedOfficer(officer);
        // Automatically assign correct department if not matched
        complaint.setDepartment(officer.getDepartment());
        complaintRepository.save(complaint);
        
        // Notify officer
        Notification notification = new Notification();
        notification.setUser(officer.getUser());
        notification.setMessage("You have been assigned to complaint: " + complaint.getTitle());
        notificationRepository.save(notification);

        return ResponseEntity.ok(new MessageResponse("Complaint assigned specifically to officer!"));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalComplaints", complaintRepository.count());
        // Simple metric
        long resolved = complaintRepository.findAll().stream()
                .filter(c -> "RESOLVED".equals(c.getStatus()) || "CLOSED".equals(c.getStatus()))
                .count();
        stats.put("resolvedComplaints", resolved);
        
        return ResponseEntity.ok(stats);
    }
}
