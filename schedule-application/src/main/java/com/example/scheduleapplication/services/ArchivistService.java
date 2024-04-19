package com.example.scheduleapplication.services;

import com.example.scheduleapplication.dtos.ArchivistDTO;
import com.example.scheduleapplication.models.Archivist;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ArchivistService {
    List<Archivist> getThoseArchivistDontWorkThisDay(LocalDate datum);

    Optional<Archivist> findById(Long selectedArchivist);

    List<Archivist> findAll();

    List<ArchivistDTO> getAllArchivists();

    ResponseEntity<?> findArchivistDTOById(Long id);

    boolean isArchivistWorkThatDay(Archivist archivist, LocalDate assignmentDay);

    List<String> getFreeArchivists(LocalDate assignmentDay);
}
