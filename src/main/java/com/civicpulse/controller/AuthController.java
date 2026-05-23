package com.civicpulse.controller;

import com.civicpulse.dto.JwtResponse;
import com.civicpulse.dto.LoginRequest;
import com.civicpulse.dto.MessageResponse;
import com.civicpulse.dto.SignupRequest;
import com.civicpulse.entity.Department;
import com.civicpulse.entity.Officer;
import com.civicpulse.entity.User;
import com.civicpulse.repository.DepartmentRepository;
import com.civicpulse.repository.OfficerRepository;
import com.civicpulse.repository.UserRepository;
import com.civicpulse.security.JwtUtil;
import com.civicpulse.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    OfficerRepository officerRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("🔐 Login attempt for: " + loginRequest.getEmail());
            
            // Validate input
            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                System.out.println("❌ Email is empty");
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Email is required"));
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                System.out.println("❌ Password is empty");
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Password is required"));
            }

            // Check if user exists
            var userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (!userOpt.isPresent()) {
                System.out.println("❌ User not found in database");
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Invalid email or password"));
            }
            
            System.out.println("✅ User found in database");
            User dbUser = userOpt.get();
            System.out.println("   - Name: " + dbUser.getName());
            System.out.println("   - Email: " + dbUser.getEmail());
            System.out.println("   - Role: " + dbUser.getRole());
            System.out.println("   - Stored password hash: " + dbUser.getPassword().substring(0, 20) + "...");

            // Attempt authentication
            System.out.println("🔓 Attempting authentication with AuthenticationManager...");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), 
                            loginRequest.getPassword()));

            System.out.println("✅ Authentication successful!");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();     
            User user = userRepository.findById(userDetails.getId()).orElseThrow();

            System.out.println("✅ JWT token generated successfully");
            return ResponseEntity.ok(new JwtResponse(jwt, 
                                                     userDetails.getId(), 
                                                     user.getName(), 
                                                     userDetails.getUsername(), 
                                                     user.getRole()));
        } catch (BadCredentialsException e) {
            System.err.println("❌ Bad credentials: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid email or password"));
        } catch (AuthenticationException e) {
            System.err.println("❌ Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid email or password"));
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Error: Login failed. Please try again."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Validate input
        if (signUpRequest.getName() == null || signUpRequest.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Name is required"));
        }
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is required"));
        }
        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Password is required"));
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRole("ROLE_CITIZEN");  // All new registrations are citizens

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
