-- Setup script to create Sanitation Officer account
-- Execute this after starting the application

-- 1. Ensure Sanitation Department exists
INSERT INTO departments (name, description, created_at, updated_at) 
VALUES ('Sanitation', 'Sanitation and Waste Management Department', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Get the Sanitation department ID for reference
SELECT @dept_id := id FROM departments WHERE name = 'Sanitation' LIMIT 1;

-- 2. Create Sanitation Officer User Account
-- Username: sanitation.officer@city.gov
-- Password: SanitationOfficer@123 (hashed with BCrypt)
-- BCrypt hash for "SanitationOfficer@123": $2a$10$K1L0pTUxG1pKZgJN5V7zXuGJhYjlsKZd8eZzHs5SZqN9QqKQJKlDu

INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
VALUES (
    'Sanitation Officer',
    'sanitation.officer@city.gov',
    '$2a$10$K1L0pTUxG1pKZgJN5V7zXuGJhYjlsKZd8eZzHs5SZqN9QqKQJKlDu',
    'ROLE_OFFICER',
    TRUE,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 3. Get the newly created user ID
SELECT @user_id := id FROM users WHERE email = 'sanitation.officer@city.gov' LIMIT 1;

-- 4. Create Officer record linking user to Sanitation department
INSERT INTO officers (user_id, department_id, created_at, updated_at)
VALUES (@user_id, @dept_id, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 5. Display the created account details
SELECT 
    u.id as 'User ID',
    u.name as 'Name',
    u.email as 'Email',
    u.role as 'Role',
    d.name as 'Department',
    'SanitationOfficer@123' as 'Password (Plaintext - for setup only)'
FROM users u
LEFT JOIN officers o ON u.id = o.user_id
LEFT JOIN departments d ON o.department_id = d.id
WHERE u.email = 'sanitation.officer@city.gov';
