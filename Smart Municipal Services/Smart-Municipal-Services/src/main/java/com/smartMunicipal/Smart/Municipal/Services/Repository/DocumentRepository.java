package com.smartMunicipal.Smart.Municipal.Services.Repository;

import com.smartMunicipal.Smart.Municipal.Services.Entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
