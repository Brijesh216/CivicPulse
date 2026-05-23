import bcrypt

# Officer passwords
officers = {
    'roads.officer@city.gov': 'RoadOfficer@2026',
    'roadmaint.officer@city.gov': 'RoadOfficer@2026',
    'water.officer@city.gov': 'WaterOfficer@2026',
    'electrical.officer@city.gov': 'ElectricalOfficer@2026',
    'sanitation.officer@city.gov': 'SanitationOfficer@2026'
}

print("Generated BCrypt Hashes for Officers\n")
print("="*80)

for email, password in officers.items():
    # Generate BCrypt hash with cost factor 10 (Spring Security default)
    salt = bcrypt.gensalt(rounds=10)
    hash_password = bcrypt.hashpw(password.encode(), salt).decode()
    
    print(f"\nEmail: {email}")
    print(f"Password: {password}")
    print(f"Hash: {hash_password}")

print("\n\n" + "="*80)
print("SQL UPDATE Statements:")
print("="*80 + "\n")

for email, password in officers.items():
    salt = bcrypt.gensalt(rounds=10)
    hash_password = bcrypt.hashpw(password.encode(), salt).decode()
    print(f"UPDATE users SET password = '{hash_password}' WHERE email = '{email}';")
