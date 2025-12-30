package com.smartMunicipal.Smart.Municipal.Services.Repository;

import com.smartMunicipal.Smart.Municipal.Services.Entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
}
