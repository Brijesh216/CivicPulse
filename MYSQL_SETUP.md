# MySQL Database Setup Guide for CivicPulse

## Quick Setup (Windows)

### 1. Install MySQL Server (if not already installed)
```
Download: https://dev.mysql.com/downloads/mysql/
Or use MySQL Installer for Windows
Choose version: 8.0 or higher (recommended: MySQL 8.0.36+)
```

### 2. Create Database and User

**Option A: Using MySQL Command Line**
```bash
# Connect to MySQL
mysql -u root -p

# Execute these commands:
CREATE DATABASE civicpulse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'civicpulse_user'@'localhost' IDENTIFIED BY 'civicpulse@123';

GRANT ALL PRIVILEGES ON civicpulse_db.* TO 'civicpulse_user'@'localhost';

FLUSH PRIVILEGES;

SHOW DATABASES;
SHOW GRANTS FOR 'civicpulse_user'@'localhost';
```

**Option B: Using MySQL Workbench**
1. Open MySQL Workbench
2. Create new database:
   - Database name: `civicpulse_db`
   - Character Set: `utf8mb4`
   - Collation: `utf8mb4_unicode_ci`
3. Create new user:
   - Username: `civicpulse_user`
   - Host: `localhost`
   - Password: `civicpulse@123`
4. Grant privileges to new database

### 3. Verify MySQL Connection

```bash
# Test connection
mysql -u civicpulse_user -p civicpulse_db

# You should see mysql> prompt
# Type: EXIT to exit
```

### 4. Start CivicPulse Application

The application will automatically create tables on startup since `spring.jpa.hibernate.ddl-auto=update`

```bash
cd c:\Users\BRIJESH R PRASAD\Documents\Projects\civicpulse
.\mvnw clean compile
.\mvnw spring-boot:run
```

### 5. Access H2 Console (Optional)

If you need to view the database:
- H2 Console: http://localhost:8081/h2-console (only if H2 is configured)
- For MySQL, use MySQL Workbench or command line

---

## Database Schema

### Tables Overview

| Table | Purpose | Key Relationships |
|-------|---------|-------------------|
| **users** | Store all users (Citizens, Officers, Admins) | Parent for complaints, notifications |
| **departments** | Store departments (Road, Water, Electrical, etc.) | Parent for officers and complaints |
| **officers** | Store officer information | Links users with departments |
| **complaints** | Store all complaints submitted | References users and departments |
| **complaint_updates** | Store status updates to complaints | References complaints and users |
| **notifications** | Store notifications for users | References users |

### Schema Design Features

✓ **Normalized Design**: Follows 3NF (Third Normal Form)
✓ **Proper Foreign Keys**: All relationships enforced at DB level
✓ **Cascade Rules**: DELETE/UPDATE cascades properly configured
✓ **Indices**: All foreign keys and common query columns indexed
✓ **Unique Constraints**: Email, department name, officer-to-user mapping unique
✓ **Timestamps**: created_at, updated_at on all tables for audit trail
✓ **UTF8MB4**: Full Unicode support for international characters
✓ **Default Values**: Proper defaults for status, timestamps, boolean fields

---

## Configuration Details

**application.properties** settings:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/civicpulse_db
spring.datasource.username=civicpulse_user
spring.datasource.password=civicpulse@123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**Connection Pool Configuration:**
- Max Pool Size: 10
- Min Idle: 5
- Connection Timeout: 20 seconds
- Idle Timeout: 5 minutes
- Max Lifetime: 20 minutes

---

## Troubleshooting

### Error: "Access denied for user 'civicpulse_user'@'localhost'"
- Verify username and password in application.properties match your MySQL user
- Check if MySQL user exists: `SELECT User, Host FROM mysql.user;`

### Error: "Unknown database 'civicpulse_db'"
- Create database: `CREATE DATABASE civicpulse_db;`
- Verify it exists: `SHOW DATABASES;`

### Error: "No suitable driver found"
- Ensure MySQL connector is in pom.xml (already included)
- Run: `.\mvnw clean install`

### Error: "Table doesn't exist"
- Delete target folder: `rd /s target` (Windows)
- Run application again - Hibernate will create tables
- Or manually run: `mysql -u civicpulse_user -p civicpulse_db < schema.sql`

### Check if tables were created
```bash
mysql -u civicpulse_user -p civicpulse_db
SHOW TABLES;
DESCRIBE users;
```

---

## Backup and Restore

### Backup Database
```bash
mysqldump -u civicpulse_user -p civicpulse_db > backup.sql
```

### Restore Database
```bash
mysql -u civicpulse_user -p civicpulse_db < backup.sql
```

---

## Additional MySQL Configurations (Optional)

### Enable Binary Logging (for replication/backup)
Edit my.ini:
```ini
[mysqld]
log-bin=mysql-bin
server-id=1
```

### Increase Max Connections
```ini
max_connections=1000
```

### Optimize Query Performance
```ini
query_cache_type=1
query_cache_size=256M
```

---

## Database Monitoring

### Check Database Size
```sql
SELECT table_schema AS 'Database', 
       ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size in MB' 
FROM information_schema.tables 
GROUP BY table_schema;
```

### Check Active Connections
```sql
SHOW PROCESSLIST;
```

### Check Slow Queries
```sql
SHOW VARIABLES LIKE 'slow_query%';
SET GLOBAL slow_query_log = 'ON';
```

---

## Next Steps

1. ✓ MySQL installed and running
2. ✓ Database and user created
3. ✓ application.properties configured
4. ✓ Start the application
5. ✓ Application creates tables automatically
6. ✓ Access application at http://localhost:8081

---

## Support

For issues with MySQL setup:
- MySQL Community: https://dev.mysql.com/
- Spring Boot JPA: https://spring.io/projects/spring-data-jpa
- Stack Overflow: Tag [mysql] and [spring-boot]
