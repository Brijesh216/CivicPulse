# Sanitation Officer Account Setup Guide

## 📋 Account Details Created

**Account Type:** Sanitation Officer (ROLE_OFFICER)

### Login Credentials:
```
Email: sanitation.officer@city.gov
Password: SanitationOfficer@123
Department: Sanitation
```

## 🚀 How to Use These Credentials

### 1. Login to the Application
- Navigate to: `http://localhost:8081/index.html`
- Click the "Login" tab
- Enter email: `sanitation.officer@city.gov`
- Enter password: `SanitationOfficer@123`
- Click "Sign In"

### 2. After Login
- You will be redirected to the Officer Dashboard
- From there you can:
  - View assigned complaints
  - Update complaint status
  - Add updates to complaints
  - Mark complaints as resolved

### 3. Admin Assignment
- The admin can assign complaints to this officer through the Admin Dashboard
- Navigate to Admin Dashboard → Assign Officer button on each complaint

---

## 📝 Setup Instructions

### Option 1: Using SQL Script (Recommended for MySQL)
If you have direct MySQL access:
```bash
mysql -u civicpulse_user -p civicpulse_db < setup_sanitation_officer.sql
```

Then enter password: `civicpulse@123`

### Option 2: Using Application REST API
```bash
curl -X POST "http://localhost:8081/api/admin/officers/create-with-credentials" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Sanitation Officer" \
  -d "email=sanitation.officer@city.gov" \
  -d "password=SanitationOfficer@123" \
  -d "departmentId=1"
```

Note: Adjust `departmentId` based on your Sanitation department ID.

### Option 3: Manual Steps via UI
1. First, ensure Sanitation department exists in the system
2. Register a temporary citizen account
3. Go to Admin Dashboard → Settings → Create Officer
4. Select the citizen user
5. Select Sanitation department
6. Click "Create Officer"
7. Have the user change their password at their next login

---

## ✅ Verification

After setup, you should be able to:
- ✓ Login with the credentials above
- ✓ See the Officer Dashboard
- ✓ View complaints assigned to the Sanitation department
- ✓ Update complaint status and add notes

---

## 🔐 Security Notes

- Change password on first login if using default credentials
- Only admins can create officer accounts
- Officer accounts are tied to specific departments
- All activities are logged with timestamps

---

## 🆘 Troubleshooting

**Issue: Login fails with "Invalid email or password"**
- Solution: Verify the email and password match exactly (case-sensitive)
- Check that the user was created successfully
- Ensure the account is active (is_active = TRUE)

**Issue: Cannot access Officer Dashboard**
- Solution: Ensure the user has ROLE_OFFICER role
- Verify the officer record exists in the officers table

**Issue: No complaints assigned**
- Solution: Admin needs to assign complaints to this officer
- Go to Admin Dashboard and use "Assign Officer" button

---

Generated: May 20, 2026
