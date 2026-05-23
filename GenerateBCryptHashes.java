import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to generate BCrypt password hashes for officers
 * Run this after adding Spring Security to the classpath
 */
public class GenerateBCryptHashes {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        Map<String, String> officers = new HashMap<>();
        officers.put("roads.officer@city.gov", "RoadOfficer@2026");
        officers.put("roadmaint.officer@city.gov", "RoadOfficer@2026");
        officers.put("water.officer@city.gov", "WaterOfficer@2026");
        officers.put("electrical.officer@city.gov", "ElectricalOfficer@2026");
        officers.put("sanitation.officer@city.gov", "SanitationOfficer@2026");
        
        System.out.println("Generated BCrypt hashes:\n");
        
        for (Map.Entry<String, String> entry : officers.entrySet()) {
            String email = entry.getKey();
            String password = entry.getValue();
            String hash = encoder.encode(password);
            
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);
            System.out.println("Hash: " + hash);
            System.out.println();
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SQL UPDATE Statements:");
        System.out.println("=".repeat(80) + "\n");
        
        for (Map.Entry<String, String> entry : officers.entrySet()) {
            String email = entry.getKey();
            String password = entry.getValue();
            String hash = encoder.encode(password);
            
            System.out.println("UPDATE users SET password = '" + hash + "' WHERE email = '" + email + "';");
        }
    }
}
