-- Setup script to update officer passwords with valid BCrypt hashes
-- Each password is properly BCrypt encoded with cost factor 10

-- Update Brijesh Officer (roads.officer@city.gov / RoadOfficer@2026)
UPDATE users SET password = '$2b$10$6FPYrl8OuLchYOQhoC/JzuMhWDGB30WFlklKWHwd3RfwyYPwvwIlS' 
WHERE email = 'roads.officer@city.gov';

-- Update Raj Officer (roadmaint.officer@city.gov / RoadOfficer@2026)
UPDATE users SET password = '$2b$10$ZwSeNfNDl7zGLlLf5ISZXeNYThpWobQe2jHdjk0JUU1VAR8ivo7eO' 
WHERE email = 'roadmaint.officer@city.gov';

-- Update Sam Officer (water.officer@city.gov / WaterOfficer@2026)
UPDATE users SET password = '$2b$10$G9XWI0L3x827eVzzaJpDF.NlxYmzpwgeDjLfxzkw5XucAPM4OQuw.' 
WHERE email = 'water.officer@city.gov';

-- Update Amit Officer (electrical.officer@city.gov / ElectricalOfficer@2026)
UPDATE users SET password = '$2b$10$qBx2yQwOxrC.IMbS7OSpTOiLDazPx5KjeRLwkLkwrVrH6ApPJSEEC' 
WHERE email = 'electrical.officer@city.gov';

-- Update Sanitation Officer (sanitation.officer@city.gov / SanitationOfficer@2026)
UPDATE users SET password = '$2b$10$zHNOndJzfo1bsTXm3h5Pae2ibO5OscZ6if0Lrv7xoIdGN0ppWXjnW' 
WHERE email = 'sanitation.officer@city.gov';

-- Display all updated officer credentials
SELECT u.name, u.email, d.name as department,
       CASE 
           WHEN u.email = 'roads.officer@city.gov' THEN 'RoadOfficer@2026'
           WHEN u.email = 'roadmaint.officer@city.gov' THEN 'RoadOfficer@2026'
           WHEN u.email = 'water.officer@city.gov' THEN 'WaterOfficer@2026'
           WHEN u.email = 'electrical.officer@city.gov' THEN 'ElectricalOfficer@2026'
           WHEN u.email = 'sanitation.officer@city.gov' THEN 'SanitationOfficer@2026'
           ELSE 'N/A'
       END as password
FROM users u
JOIN officers o ON u.id = o.user_id
JOIN departments d ON o.department_id = d.id
ORDER BY d.name, u.name;
