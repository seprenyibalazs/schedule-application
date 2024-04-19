package com.example.scheduleapplication.services;

import com.example.scheduleapplication.dtos.DayAndArchivistIdDTO;
import com.example.scheduleapplication.dtos.ModifyScheduleDTO;
import com.example.scheduleapplication.dtos.ScheduleDTO;
import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.models.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    void createSchedule(Archivist archivist, Long channelId, LocalDate broadcast, LocalDate datum);

    List<Schedule> findScheduleByDay(LocalDate day);

    List<Schedule> findScheduleByDayAndArchivist(Archivist archivist, LocalDate localMonday);

    void modifyTheSchedule(Long idOfSchedule, LocalDate datum, Long archivistId);

    List<ScheduleDTO> getAllSchedules();

    Optional<Schedule> findById(Long id);

    List<String> checkTheErrors(ModifyScheduleDTO modifyScheduleDTO);

    List<String> nullCheckDto(ModifyScheduleDTO modifyScheduleDTO);

    List<ScheduleDTO> getSchedulesByDate(LocalDate currentMonday);

    List<ScheduleDTO> getSchedulesByDateAndArchivistId(LocalDate currentMonday, Long id);

    List<String> checkDayAndArchivist(DayAndArchivistIdDTO dayAndArchivistIdDTO);
}
