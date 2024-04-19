package com.example.scheduleapplication.controllers;

import com.example.scheduleapplication.dtos.*;
import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.models.Schedule;
import com.example.scheduleapplication.services.ArchivistService;
import com.example.scheduleapplication.services.ChannelService;
import com.example.scheduleapplication.services.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final ArchivistService archivistServices;
    private final ChannelService channelService;
    private final ScheduleService scheduleService;

    @GetMapping("/all-archivists")
    public ResponseEntity<?> getAllArchivists(){
        List<ArchivistDTO> archivists = archivistServices.getAllArchivists();

        if(archivists.isEmpty()){
            return ResponseEntity.ok().body("Nincs regisztrált archivátor, így a list üres.");
        }

        return ResponseEntity.ok(archivists);
    }

    @GetMapping("/get-archivist-by-id")
    public ResponseEntity<?> getArchivistById(@RequestBody(required = false) IdDTO idDTO){
        if(idDTO == null || idDTO.getId() == null){
            return ResponseEntity.badRequest().body("Nem adott meg id-t.");
        }

        return archivistServices.findArchivistDTOById(idDTO.getId());
    }

    @GetMapping("/all-channels")
    public ResponseEntity<?> getAllChannels(){
        List<ChannelDTO> channels = channelService.getAllChannels();

        if(channels.isEmpty()){
            return ResponseEntity.ok().body("Nincs feltöltve csatorna, így a list üres.");
        }

        return ResponseEntity.ok(channels);
    }

    @GetMapping("/all-schedules")
    public ResponseEntity<?> getAllSchedules(){
        List<ScheduleDTO> schedules = scheduleService.getAllSchedules();

        if(schedules.isEmpty()){
            return ResponseEntity.badRequest().body("Jelenleg nincs archivátorhoz rendelve egyetlen csatorna sem, így a lista üres.");
        }

        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/modify-schedule")
    public ResponseEntity<?> modifySchedule(@RequestBody(required = false) ModifyScheduleDTO modifyScheduleDTO){
        if(modifyScheduleDTO == null){
            return ResponseEntity.badRequest().body("Nincs megedva schedule.");
        }
        List<String> dtoError = scheduleService.nullCheckDto(modifyScheduleDTO);
        if(!dtoError.isEmpty()){
            String message = "Hiányzó adatok: ".concat(String.join(", ", dtoError));
            return ResponseEntity.badRequest().body(message);
        }

        List<String> error = scheduleService.checkTheErrors(modifyScheduleDTO);
        if(!error.isEmpty()){
            String message = "Kijavítandó hibák: ".concat(String.join(", ", error));
            return ResponseEntity.badRequest().body(message);
        }
        Optional<Archivist> archivistOptional = archivistServices.findById(modifyScheduleDTO.getArchivistId());

        boolean isArchivistWorkThatDay = archivistServices.isArchivistWorkThatDay(archivistOptional.get(), modifyScheduleDTO.getAssignmentDay());
        if(isArchivistWorkThatDay){
            List<String> freeArchivists = archivistServices.getFreeArchivists(modifyScheduleDTO.getAssignmentDay());
            String message = "A kiválsztott archivátor már dolgozik ezen a napon, kérlek válassz egy másik archivátort. Elérhető munkatársak: ".concat(String.join(", ", freeArchivists));
            return ResponseEntity.badRequest().body(message);
        }

        Optional<Schedule> scheduleOptional = scheduleService.findById(modifyScheduleDTO.getId());

        if(scheduleOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Nincs ilyen schedule.");
        }

        if(!scheduleOptional.get().getBroadcast().equals(modifyScheduleDTO.getBroadcast())){
            return ResponseEntity.badRequest().body("Schedule-ben lévő adásnap nem egyezik a megadott sogárzás napjával! A Schedulben megadott adásnap: " + scheduleOptional.get().getBroadcast());
        }

        scheduleService.modifyTheSchedule(scheduleOptional.get().getId(), modifyScheduleDTO.getAssignmentDay(), modifyScheduleDTO.getArchivistId());

        return ResponseEntity.ok().body("Schedule sikeresen módosítva.");
    }

    @GetMapping("/get-week")
    public ResponseEntity<?> getSchedule(@RequestBody(required = false) DayDTO dayDTO){
        if(dayDTO == null){
            return ResponseEntity.badRequest().body("Nincs dátum megadva.");
        }
        LocalDate currentMonday = dayDTO.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<ScheduleDTO> scheduleDTOS = scheduleService.getSchedulesByDate(currentMonday);
        if(scheduleDTOS.isEmpty()){
            return ResponseEntity.ok().body("Jelenleg üres még ezen a héten a munkabeosztás.");
        }

        return ResponseEntity.ok(scheduleDTOS);
    }

    @GetMapping("/get-week-filtered-by-id")
    public ResponseEntity<?> getScheduleByArchivistId(@RequestBody(required = false) DayAndArchivistIdDTO dayAndArchivistIdDTO){
        if(dayAndArchivistIdDTO == null){
            return ResponseEntity.badRequest().body("Nincs dátum és archivator id megadva.");
        }

        List<String> error = scheduleService.checkDayAndArchivist(dayAndArchivistIdDTO);
        if(!error.isEmpty()){
            String message = ("Hibás adatok: ".concat(String.join(", ", error)));
            return ResponseEntity.badRequest().body(message);
        }

        LocalDate currentMonday = dayAndArchivistIdDTO.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<ScheduleDTO> scheduleDTOS = scheduleService.getSchedulesByDateAndArchivistId(currentMonday, dayAndArchivistIdDTO.getArchivistId());
        if(scheduleDTOS.isEmpty()){
            return ResponseEntity.ok().body("Az archivátor naptára üres.");
        }

        return ResponseEntity.ok(scheduleDTOS);
    }
}
