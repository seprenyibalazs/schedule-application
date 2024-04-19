package com.example.scheduleapplication.repositories;

import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByBroadcast(LocalDate date);

    List<Schedule> findByAssignmentDay(LocalDate day);

    List<Schedule> findByAssignmentDayAndArchivist(LocalDate assigment, Archivist archivist);

    List<Schedule> findByAssignmentDayBetween(LocalDate currentMonday, LocalDate nextFriday);
}
