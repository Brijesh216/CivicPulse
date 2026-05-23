package com.civicpulse.controller;

import com.civicpulse.dto.MessageResponse;
import com.civicpulse.entity.Notification;
import com.civicpulse.entity.User;
import com.civicpulse.repository.NotificationRepository;
import com.civicpulse.repository.UserRepository;
import com.civicpulse.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        try {
            User user = getCurrentUser();
            System.out.println("📬 Fetching notifications for user: " + user.getName());
            List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            System.out.println("✅ Found " + notifications.size() + " notifications");
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("❌ Error fetching notifications: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        try {
            User user = getCurrentUser();
            System.out.println("📬 Fetching unread notifications for user: " + user.getName());
            List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
            System.out.println("✅ Found " + unreadNotifications.size() + " unread notifications");
            return ResponseEntity.ok(unreadNotifications);
        } catch (Exception e) {
            System.err.println("❌ Error fetching unread notifications: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUnreadCount() {
        try {
            User user = getCurrentUser();
            long count = notificationRepository.countByUserIdAndIsReadFalse(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("unreadCount", count);
            System.out.println("📬 Unread notification count for user " + user.getName() + ": " + count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error counting unread notifications: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable("id") Long id) {
        try {
            User user = getCurrentUser();
            Notification notification = notificationRepository.findById(id).orElseThrow();
            
            // Verify notification belongs to current user
            if (!notification.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(new MessageResponse("Not authorized to update this notification"));
            }
            
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
            System.out.println("✅ Marked notification " + id + " as read");
            
            return ResponseEntity.ok(new MessageResponse("Notification marked as read"));
        } catch (Exception e) {
            System.err.println("❌ Error marking notification as read: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error updating notification"));
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        try {
            User user = getCurrentUser();
            List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
            
            for (Notification notification : unreadNotifications) {
                notification.setRead(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
            System.out.println("✅ Marked " + unreadNotifications.size() + " notifications as read for user " + user.getName());
            
            return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
        } catch (Exception e) {
            System.err.println("❌ Error marking all notifications as read: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error updating notifications"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable("id") Long id) {
        try {
            User user = getCurrentUser();
            Notification notification = notificationRepository.findById(id).orElseThrow();
            
            // Verify notification belongs to current user
            if (!notification.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(new MessageResponse("Not authorized to delete this notification"));
            }
            
            notificationRepository.delete(notification);
            System.out.println("✅ Deleted notification " + id);
            return ResponseEntity.ok(new MessageResponse("Notification deleted"));
        } catch (Exception e) {
            System.err.println("❌ Error deleting notification: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error deleting notification"));
        }
    }
}
