# MySQL Setup Checklist - Quick Reference

## Quick Setup in 10 Minutes

### ✅ Step 1: Install MySQL (Already Have It? Skip to Step 2)
- [ ] Download MySQL 8.0+ from https://dev.mysql.com/downloads/mysql/
- [ ] Run installer
- [ ] Choose: Server Machine → Development Machine → Default
- [ ] Use port: 3306
- [ ] User: root, Password: (set one)

### ✅ Step 2: Create Database & User (5 min)

**Windows Command Prompt or PowerShell**:
```bash
# Connect to MySQL
mysql -u root -p

# Paste these commands one by one:

CREATE DATABASE civicpulse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'civicpulse_user'@'localhost' IDENTIFIED BY 'civicpulse@123';

GRANT ALL PRIVILEGES ON civicpulse_db.* TO 'civicpulse_user'@'localhost';

FLUSH PRIVILEGES;

# Exit MySQL
EXIT;
```

### ✅ Step 3: Verify Connection (2 min)
```bash
# Test if user can connect
mysql -u civicpulse_user -p civicpulse_db

# Should see: mysql>
# Type: EXIT
```

### ✅ Step 4: Start CivicPulse (3 min)
```bash
# Open PowerShell and navigate to project
cd c:\Users\BRIJESH R PRASAD\Documents\Projects\civicpulse

# Start application
.\mvnw spring-boot:run
```

✅ **Done!** Application running at http://localhost:8081

---

## Configuration Already Done

| Item | Status |
|------|--------|
| application.properties | ✅ Updated to MySQL |
| Entity classes | ✅ Enhanced with indices |
| schema.sql | ✅ Created |
| Documentation | ✅ Complete |

---

## What Gets Created Automatically

When you start the application:
- [ ] Tables created (users, complaints, departments, etc.)
- [ ] Indices created (15 total for performance)
- [ ] Foreign keys created
- [ ] Sample departments inserted

---

## Post-Setup Verification

```bash
# Connect to MySQL and verify
mysql -u civicpulse_user -p civicpulse_db

# Run these checks:
SHOW TABLES;
# Should show: 6 tables

DESCRIBE users;
# Should show columns: id, name, email, password, role, phone_number, is_active, created_at, updated_at

SHOW INDEXES FROM complaints;
# Should show 8 indices

SELECT COUNT(*) FROM departments;
# Should return: 4 (pre-populated departments)

EXIT;
```

---

## If Something Goes Wrong

### Error: "Access denied for user"
```bash
# Check username/password in application.properties
# It should be:
# spring.datasource.username=civicpulse_user
# spring.datasource.password=civicpulse@123

# Verify user was created:
mysql -u root -p
SELECT User, Host FROM mysql.user;
```

### Error: "Unknown database"
```bash
# Create database:
mysql -u root -p
CREATE DATABASE civicpulse_db;
EXIT;
```

### Error: "No suitable driver found"
```bash
# Rebuild Maven:
cd c:\Users\BRIJESH R PRASAD\Documents\Projects\civicpulse
.\mvnw clean install
```

### Tables not created
```bash
# Check logs during startup - look for:
# "Hibernate: create table users..."
# If not present, check spring.jpa.hibernate.ddl-auto=update in application.properties
```

---

## Connection String Reference

```properties
URL:      jdbc:mysql://localhost:3306/civicpulse_db
User:     civicpulse_user
Password: civicpulse@123
Port:     3306
Database: civicpulse_db
```

---

## MySQL Service Status

### Windows Services
```bash
# Start MySQL service (if it stops)
net start MySQL80

# Stop MySQL service
net stop MySQL80
```

### Check if MySQL is running
```bash
# This command will work if MySQL is running:
mysql -u root -p

# Connection refused = MySQL not running
```

---

## Need Help?

### Where to Find Help
1. **Installation Issues**: MYSQL_SETUP.md (detailed guide)
2. **Schema Questions**: DATABASE_SCHEMA.md (technical docs)
3. **Implementation Details**: MYSQL_INTEGRATION_SUMMARY.md (what changed)
4. **General Spring Boot**: https://spring.io/projects/spring-data-jpa

### Common Questions

**Q: Where's the H2 console?**
A: H2 is removed. For MySQL, use MySQL Workbench or command line.

**Q: Can I still use H2?**
A: Yes, restore original application.properties and run `.\mvnw spring-boot:run`

**Q: How do I backup data?**
A: `mysqldump -u civicpulse_user -p civicpulse_db > backup.sql`

**Q: Can I run MySQL on different server?**
A: Yes, change `localhost` to server IP in application.properties

**Q: What if I forget the password?**
A: MySQL root can reset it. See MySQL docs or reinstall.

---

## Performance Tips

1. **Indices**: Already configured - no action needed
2. **Connection Pool**: Set to 10 max connections - adjust based on load
3. **Logging**: Set to INFO for production, DEBUG for development
4. **Hibernate**: Set ddl-auto to `validate` in production

---

## Backup Strategy

### Weekly Backup
```bash
# Create backup
mysqldump -u civicpulse_user -p civicpulse_db > civicpulse_backup_$(date +%Y%m%d).sql

# Restore backup
mysql -u civicpulse_user -p civicpulse_db < civicpulse_backup_20240414.sql
```

---

## Files to Review After Setup

1. ✅ **MYSQL_SETUP.md** - How to install and configure MySQL
2. ✅ **DATABASE_SCHEMA.md** - Complete schema documentation
3. ✅ **MYSQL_INTEGRATION_SUMMARY.md** - What was changed in code
4. ✅ **schema.sql** - Raw SQL schema (can run manually if needed)
5. ✅ **application.properties** - Database connection settings

---

## Ready?

```bash
# Run this command to start:
cd c:\Users\BRIJESH R PRASAD\Documents\Projects\civicpulse && .\mvnw spring-boot:run

# Then open: http://localhost:8081
```

---

**Total Time: 10-15 minutes from start to working application!**
