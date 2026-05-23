package com.civicpulse.repository;

import com.civicpulse.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
    List<Complaint> findByDepartmentIdOrderByCreatedAtDesc(Long departmentId);
    List<Complaint> findByAssignedOfficerIdOrderByCreatedAtDesc(Long officerId);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}
