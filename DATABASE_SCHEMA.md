# CivicPulse Database Schema Documentation

## Overview

The CivicPulse database follows industry-standard design principles with normalized schema, proper relationships, and optimized indices for performance.

---

## Entity Relationship Diagram

```
┌─────────────┐
│   users     │◄─────┬──── One-to-Many ───────┐
├─────────────┤      │                        │
│ id (PK)     │      │                        │
│ email       │      │                        │
│ role        │      │                        │
│ is_active   │      │                        │
└─────────────┘      │                   ┌────────────────┐
                     ├──── One-to-One   │ officers      │
                     │                   ├────────────────┤
                     │                   │ id (PK)        │
                     │                   │ user_id (FK,U) │
                     │                   │ department_id  │
                     │                   └────────────────┘
                     │                         │
                     │                    Many-to-One
                     │                         │
                     │                    ┌────────────────┐
                     │                    │ departments    │
                     │                    ├────────────────┤
                     │                    │ id (PK)        │
                     │                    │ name (Unique)  │
                     │                    └────────────────┘
                     │
                     ├──── One-to-Many ────► complaints
                     │                   ├────────────────┐
                     │                   │ id (PK)        │
                     │                   │ created_by_user│
                     │                   │ department_id  │
                     │                   │ assigned_officer│
                     │                   │ status         │
                     │                   │ priority       │
                     │                   └────────────────┘
                     │                         │
                     │                    One-to-Many
                     │                         │
                     │                   ┌──────────────────┐
                     │                   │ complaint_updates│
                     │                   ├──────────────────┤
                     │                   │ id (PK)          │
                     │                   │ complaint_id(FK) │
                     │                   │ updated_by_user  │
                     │                   └──────────────────┘
                     │
                     └──── One-to-Many ────► notifications
                                        ├────────────────────┐
                                        │ id (PK)            │
                                        │ user_id (FK)       │
                                        │ message            │
                                        │ is_read            │
                                        └────────────────────┘
```

---

## Table Details

### 1. users (Core User Table)

**Primary Purpose**: Store all application users across roles

**Columns**:
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique user identifier |
| name | VARCHAR(100) | NOT NULL | User's full name |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Email address, unique per user |
| password | VARCHAR(255) | NOT NULL | Bcrypt-hashed password |
| role | VARCHAR(20) | NOT NULL | ROLE_CITIZEN, ROLE_OFFICER, ROLE_ADMIN |
| phone_number | VARCHAR(15) | - | Optional contact number |
| is_active | BOOLEAN | NOT NULL, DEFAULT=TRUE | Account active status |
| created_at | TIMESTAMP | NOT NULL, Auto | Account creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, Auto | Last update timestamp |

**Indices**:
- `idx_users_email`: Unique index for fast login lookups
- `idx_users_role`: Query users by role (citizens, officers, etc.)
- `idx_users_is_active`: Query active users only
- `idx_users_created_at`: Timeline queries

**Sample Data**:
```sql
INSERT INTO users VALUES (1, 'Raj Kumar', 'raj@example.com', '$2a$10...', 'ROLE_CITIZEN', '9876543210', 1, NOW(), NOW());
INSERT INTO users VALUES (2, 'Officer Singh', 'singh@example.com', '$2a$10...', 'ROLE_OFFICER', '9876543211', 1, NOW(), NOW());
INSERT INTO users VALUES (3, 'Admin User', 'admin@example.com', '$2a$10...', 'ROLE_ADMIN', '9876543212', 1, NOW(), NOW());
```

---

### 2. departments (Department/Service Categories)

**Primary Purpose**: Categorize and manage service departments

**Columns**:
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique department ID |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Department name (Road, Water, etc.) |
| description | VARCHAR(500) | - | Department details |
| created_at | TIMESTAMP | NOT NULL, Auto | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, Auto | Last update timestamp |

**Indices**:
- `idx_departments_name`: Unique lookup by department name

**Sample Data**:
```sql
INSERT INTO departments VALUES (1, 'Road Maintenance', 'Handles potholes and road damage.', NOW(), NOW());
INSERT INTO departments VALUES (2, 'Water Supply', 'Handles water leakage and shortage.', NOW(), NOW());
INSERT INTO departments VALUES (3, 'Electrical', 'Handles street lights and power outages.', NOW(), NOW());
INSERT INTO departments VALUES (4, 'Sanitation', 'Handles garbage collection and cleanliness.', NOW(), NOW());
```

---

### 3. officers (Officer-Department Mapping)

**Primary Purpose**: Link officers to departments and users

**Columns**:
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique officer record ID |
| user_id | BIGINT | FK→users.id, UNIQUE | One-to-One with user (ROLE_OFFICER) |
| department_id | BIGINT | FK→departments.id, NOT NULL | Which department officer serves |
| created_at | TIMESTAMP | NOT NULL, Auto | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, Auto | Last update timestamp |

**Indices**:
- `idx_officers_user_id`: Unique lookup of officer by user
- `idx_officers_department_id`: Query officers in a department

**Relationships**:
- One Officer → One User (user_id unique)
- One Officer → One Department (many officers per department)

**Cascade Rules**:
- ON DELETE CASCADE: Deleting user deletes officer record
- ON DELETE RESTRICT: Cannot delete department if officers assigned

**Sample Data**:
```sql
INSERT INTO officers VALUES (1, 2, 1, NOW(), NOW()); -- Officer Singh works in Road Maintenance
```

---

### 4. complaints (Main Complaint Records)

**Primary Purpose**: Store citizen complaints with full tracking information

**Columns**:
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique complaint ID |
| title | VARCHAR(255) | NOT NULL | Brief complaint summary |
| description | VARCHAR(1000) | NOT NULL | Full complaint details |
| photo_url | VARCHAR(500) | - | Image URL of the issue |
| priority | VARCHAR(20) | - | LOW, MEDIUM, HIGH, CRITICAL |
| status | VARCHAR(20) | NOT NULL, DEFAULT='PENDING' | PENDING, IN_PROGRESS, RESOLVED, CLOSED |
| area | VARCHAR(255) | NOT NULL | Location of the complaint |
| created_by_user_id | BIGINT | FK→users.id, NOT NULL | Which citizen filed complaint |
| department_id | BIGINT | FK→departments.id | Which department handles complaint |
| assigned_officer_id | BIGINT | FK→officers.id | Officer assigned to handle |
| created_at | TIMESTAMP | NOT NULL, Auto | Complaint filing timestamp |
| updated_at | TIMESTAMP | NOT NULL, Auto | Last update timestamp |
| resolved_at | TIMESTAMP | - | When complaint was resolved |

**Indices** (for optimal query performance):
- `idx_complaints_created_by_user_id`: Query user's complaints
- `idx_complaints_department_id`: Query complaints by department
- `idx_complaints_assigned_officer_id`: Query officer's assigned complaints
- `idx_complaints_status`: Query complaints by status
- `idx_complaints_priority`: Query by priority level
- `idx_complaints_created_at`: Timeline queries
- `idx_complaints_status_department`: Combined query optimization
- `idx_complaints_created_by_status`: User's complaints by status

**Cascade Rules**:
- ON DELETE CASCADE: Deleting user/department deletes complaints
- ON DELETE SET NULL: Unassigning officer doesn't delete complaint

**Frequent Queries**:
```sql
-- Get all complaints by a citizen
SELECT * FROM complaints WHERE created_by_user_id = ? AND status != 'CLOSED';

-- Get pending complaints in a department
SELECT * FROM complaints WHERE department_id = ? AND status = 'PENDING' ORDER BY priority DESC;

-- Get officer's assigned complaints
SELECT * FROM complaints WHERE assigned_officer_id = ? AND status IN ('PENDING', 'IN_PROGRESS');
```

---

### 5. complaint_updates (Complaint History/Status Updates)

**Primary Purpose**: Maintain audit trail and status history of complaints

**Columns**:
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique update ID |
| complaint_id | BIGINT | FK→complaints.id, NOT NULL | Which complaint is updated |
| updated_by_user_id | BIGINT | FK→users.id, NOT NULL | Officer/Admin who updated |
| update_text | VARCHAR(1000) | NOT NULL | Status update message |
| photo_url | VARCHAR(500) | - | Updated image/progress photo |
| created_at | TIMESTAMP | NOT NULL, Auto | Update timestamp |

**Indices**:
- `idx_complaint_updates_complaint_id`: Query all updates for a complaint
- `idx_complaint_updates_updated_by_user_id`: Query updates by officer
- `idx_complaint_updates_created_at`: Timeline queries

**Cascade Rules**:
- ON DELETE CASCADE: Deleting complaint deletes all updates
- ON DELETE CASCADE: Deleting user deletes their updates

**Typical Usage**:
```sql
-- Get complaint history
SELECT * FROM complaint_updates WHERE complaint_id = ? ORDER BY created_at DESC;

-- Check who updated what and when
SELECT cu.*, u.name FROM complaint_updates cu 
JOIN users u ON cu.updated_by_user_id = u.id 
WHERE cu.complaint_id = ?;
```

---

### 6. notifications (User Notifications)

**Primary Purpose**: Store notifications for users about complaints and updates

**Columns**:
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Unique notification ID |
| user_id | BIGINT | FK→users.id, NOT NULL | Which user receives notification |
| message | VARCHAR(500) | NOT NULL | Notification text |
| is_read | BOOLEAN | NOT NULL, DEFAULT=FALSE | Read/unread status |
| created_at | TIMESTAMP | NOT NULL, Auto | Notification created timestamp |
| read_at | TIMESTAMP | - | When user read it |

**Indices**:
- `idx_notifications_user_id`: Get all notifications for a user
- `idx_notifications_is_read`: Get unread notifications
- `idx_notifications_created_at`: Recent notifications
- `idx_notifications_user_is_read`: Unread notifications for user (most common query)

**Cascade Rules**:
- ON DELETE CASCADE: Deleting user deletes their notifications

**Common Queries**:
```sql
-- Get unread notifications for a user
SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC;

-- Get user's recent notifications
SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 10;

-- Mark as read
UPDATE notifications SET is_read = TRUE, read_at = NOW() WHERE id = ?;
```

---

## Query Optimization Strategies

### 1. Fast Lookups by User Role
```sql
-- Get all officers (uses idx_users_role)
SELECT u.* FROM users u WHERE u.role = 'ROLE_OFFICER';

-- Get active officers
SELECT u.* FROM users u WHERE u.role = 'ROLE_OFFICER' AND u.is_active = TRUE;
```

### 2. Department Status Reports
```sql
-- Complaints by department and status (uses composite index)
SELECT department_id, status, COUNT(*) 
FROM complaints 
GROUP BY department_id, status;

-- Pending complaints in specific department (uses composite index)
SELECT * FROM complaints 
WHERE department_id = ? AND status = 'PENDING' 
ORDER BY priority DESC, created_at ASC;
```

### 3. User Activity Timeline
```sql
-- User's complaint history with updates
SELECT c.*, COUNT(cu.id) as update_count
FROM complaints c
LEFT JOIN complaint_updates cu ON c.id = cu.complaint_id
WHERE c.created_by_user_id = ?
GROUP BY c.id
ORDER BY c.created_at DESC;
```

### 4. Officer Workload
```sql
-- Officer's assigned complaints by status
SELECT status, COUNT(*) as count
FROM complaints
WHERE assigned_officer_id = ?
GROUP BY status;
```

### 5. Notification Management
```sql
-- User's unread notification count
SELECT COUNT(*) FROM notifications 
WHERE user_id = ? AND is_read = FALSE;

-- Recent activity notifications
SELECT n.* FROM notifications n
WHERE n.user_id = ? AND n.created_at > NOW() - INTERVAL 7 DAY
ORDER BY n.created_at DESC;
```

---

## Data Integrity Rules

### Foreign Key Constraints
1. **users → complaints (created_by_user_id)**
   - Rule: DELETE CASCADE (user deleted → complaints deleted)
   
2. **departments → complaints (department_id)**
   - Rule: SET NULL (can unassign department, don't delete complaints)
   
3. **officers → complaints (assigned_officer_id)**
   - Rule: SET NULL (can unassign officer, don't delete complaints)
   
4. **user → officers (user_id)**
   - Rule: DELETE CASCADE (unique constraint ensures 1:1)

### Unique Constraints
- `users.email`: Email must be globally unique
- `departments.name`: Department names must be unique
- `officers.user_id`: One officer per user (enforces role-based link)

---

## Performance Tuning

### Index Benefits
- **Full table scans eliminated**: Queries with WHERE on indexed columns use index
- **Composite indices**: Multiple WHERE conditions optimized
- **Foreign key indices**: JOIN operations accelerated
- **Unique indices**: Email lookup (login) is O(1)

### Connection Pool
- **Max Connections**: 10 (adjust based on load)
- **Min Idle**: 5 (always available)
- **Timeout**: 20 seconds (prevents long waiting)

### Query Caching
For high-traffic scenarios, consider:
```sql
-- Enable query cache
SET GLOBAL query_cache_type = 1;
SET GLOBAL query_cache_size = 256 * 1024 * 1024;
```

---

## Backup Strategy

### Daily Backup
```bash
mysqldump -u civicpulse_user -p civicpulse_db > backup_$(date +%Y%m%d).sql
```

### Weekly Full Backup + Binlog
```bash
# MySQL automatically logs binary changes if configured
# Use mysqldump + binlog for point-in-time recovery
```

### Retention Policy
- Daily backups: Keep 7 days
- Weekly backups: Keep 4 weeks
- Monthly backups: Keep 12 months

---

## Monitoring Queries

### Database Size
```sql
SELECT table_name, ROUND((data_length + index_length) / 1024 / 1024, 2) as size_mb
FROM information_schema.tables
WHERE table_schema = 'civicpulse_db'
ORDER BY (data_length + index_length) DESC;
```

### Index Usage
```sql
SELECT object_schema, object_name, count_read, count_insert, count_update, count_delete
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = 'civicpulse_db'
ORDER BY count_read DESC;
```

### Slow Queries
```sql
-- Check slow query log (requires configuration)
SELECT * FROM performance_schema.events_statements_summary_by_digest
ORDER BY SUM_TIMER_WAIT DESC LIMIT 10;
```

---

## Next Steps

1. Run `schema.sql` to create tables
2. Start application (Hibernate auto-creates if not present)
3. Monitor logs for any schema errors
4. Verify all indices are created: `SHOW INDEXES FROM table_name;`
5. Run monitoring queries to ensure performance

---
