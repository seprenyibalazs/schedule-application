package com.example.scheduleapplication.repositories;

import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivistRepository extends JpaRepository<Archivist, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.archivist = :archivist AND s.assignmentDay = :assignmentDay")
    Optional<Schedule> isArchivistWorkThatDay(@Param("archivist") Archivist archivist, @Param("assignmentDay") LocalDate assignmentDay);

    @Query("SELECT DISTINCT a FROM Archivist a WHERE NOT EXISTS (SELECT s FROM Schedule s WHERE s.archivist = a AND (s.assignmentDay = :assignmentDay OR s.assignmentDay IS NULL))")
    List<Archivist> findNoMatchAssignmentDay(@Param("assignmentDay") LocalDate assignmentDay);
}
