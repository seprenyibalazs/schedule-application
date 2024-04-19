package com.example.scheduleapplication.repositories;

import com.example.scheduleapplication.models.Archivist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivistRepository extends JpaRepository<Archivist, Long> {
}
