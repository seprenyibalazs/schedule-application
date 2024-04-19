package com.example.scheduleapplication.services;

import com.example.scheduleapplication.dtos.ArchivistDTO;
import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.repositories.ArchivistRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ArchivistServiceImpl implements ArchivistService{

    private final ArchivistRepository archivistRepository;

    @Override
    public List<Archivist> getThoseArchivistDontWorkThisDay(LocalDate datum) {
        List<Archivist> archivistList = archivistRepository.findAll();

        List<Archivist> filteredArchivists = archivistList.stream()
                .filter(archivist -> archivist.getSchedules().stream()
                        .noneMatch(schedule -> schedule.getAssignmentDay().equals(datum)))
                .collect(Collectors.toList());

        return filteredArchivists;
    }

    @Override
    public Optional<Archivist> findById(Long selectedArchivist) {
        return archivistRepository.findById(selectedArchivist);
    }

    @Override
    public List<Archivist> findAll() {
        return archivistRepository.findAll();
    }

    @Override
    public List<ArchivistDTO> getAllArchivists() {
        List<Archivist> archivistList = archivistRepository.findAll();

        return archivistList.stream()
                .map(archivist -> new ArchivistDTO(archivist.getId(), archivist.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> findArchivistDTOById(Long id) {
        Optional<Archivist> archivistOptional = findById(id);

        if(archivistOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Nincs ilyen id-val rendelkező archivátor.");
        }
        Optional<ArchivistDTO> archivistDTO = Optional.of(new ArchivistDTO(archivistOptional.get().getId(), archivistOptional.get().getName()));

        return ResponseEntity.ok(archivistDTO);
    }

    @Override
    public boolean isArchivistWorkThatDay(Archivist archivist, LocalDate assignmentDay) {
        return archivist.getSchedules().stream()
                .anyMatch(schedule -> schedule.getAssignmentDay().equals(assignmentDay));
    }

    @Override
    public List<String> getFreeArchivists(LocalDate assignmentDay) {
        List<Archivist> archivistList = getThoseArchivistDontWorkThisDay(assignmentDay);

        return archivistList.stream()
                .map(Archivist::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }
}