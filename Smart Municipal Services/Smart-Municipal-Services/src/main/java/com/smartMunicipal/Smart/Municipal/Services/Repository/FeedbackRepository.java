package com.smartMunicipal.Smart.Municipal.Services.Repository;

import com.smartMunicipal.Smart.Municipal.Services.Entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
