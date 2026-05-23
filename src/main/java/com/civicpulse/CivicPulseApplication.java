package com.civicpulse;

import com.civicpulse.entity.Department;
import com.civicpulse.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class CivicPulseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CivicPulseApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(DepartmentRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				Department d1 = new Department();
				d1.setName("Road Maintenance");
				d1.setDescription("Handles potholes and road damage.");

				Department d2 = new Department();
				d2.setName("Water Supply");
				d2.setDescription("Handles water leakage and shortage.");

				Department d3 = new Department();
				d3.setName("Electrical");
				d3.setDescription("Handles street lights and power outages.");

				Department d4 = new Department();
				d4.setName("Sanitation");
				d4.setDescription("Handles garbage collection and cleanliness.");

				repository.saveAll(Arrays.asList(d1, d2, d3, d4));
			}
		};
	}
}
