# CivicPulse - Quick Fix Guide

## Problem
Build fails with compilation error in `SecurityConfig.java` due to Spring Security 6.x API changes.

## Solution

### Edit File: `src/main/java/com/civicpulse/security/SecurityConfig.java`

Replace lines 29-33 (the `authenticationProvider()` method):

**FROM (Broken - Lines 29-33):**
```java
@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(this.userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}
```

**TO (Fixed):**
```java
@Bean
public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}
```

## What Changed
1. Changed constructor from `new DaoAuthenticationProvider(this.userDetailsService)` to `new DaoAuthenticationProvider()`
2. Added explicit `setUserDetailsService(userDetailsService)` call
3. This aligns with Spring Security 6.x API (used by Spring Boot 4.0.4)

## How to Apply
1. Open: `src/main/java/com/civicpulse/security/SecurityConfig.java`
2. Go to line 30
3. Replace the method as shown above
4. Save the file

## Verify Fix
```bash
cd c:\Users\BRIJESH R PRASAD\Documents\Projects\civicpulse
mvnw.cmd clean package -DskipTests
```

Expected result: BUILD SUCCESS

## Start Application
```bash
mvnw.cmd spring-boot:run
```

Application will start on http://localhost:8081

## Test Application
```bash
# Get departments
curl http://localhost:8081/api/departments

# Register user
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","password":"Test@123","role":"ROLE_CITIZEN"}'

# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test@123"}'
```

---
**This is the ONLY fix needed to get the application building and running.**
