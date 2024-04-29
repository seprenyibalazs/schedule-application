package com.example.scheduleapplication.services;

import com.example.scheduleapplication.dtos.DayAndArchivistIdDTO;
import com.example.scheduleapplication.dtos.ModifyScheduleDTO;
import com.example.scheduleapplication.dtos.ScheduleDTO;
import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.models.Channel;
import com.example.scheduleapplication.models.Schedule;
import com.example.scheduleapplication.repositories.ArchivistRepository;
import com.example.scheduleapplication.repositories.ChannelRepository;
import com.example.scheduleapplication.repositories.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleRepository scheduleRepository;
    private final ChannelRepository chanelRepository;
    private final ArchivistRepository archivistRepository;


    @Override
    public void createSchedule(Archivist archivist, Long channelId, LocalDate broadcast, LocalDate datum) {
        Optional<Channel> channel = chanelRepository.findById(channelId);

        Schedule schedule = new Schedule();
        schedule.setArchivist(archivist);
        schedule.setChanel(channel.get());
        schedule.setBroadcast(broadcast);
        schedule.setAssignmentDay(datum);

        scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> findScheduleByDay(LocalDate day) {
        return scheduleRepository.findByAssignmentDay(day);
    }

    @Override
    public List<Schedule> findScheduleByDayAndArchivist(Archivist archivist, LocalDate assigment) {
        return scheduleRepository.findByAssignmentDayAndArchivist(assigment, archivist);
    }

    @Override
    public void modifyTheSchedule(Long idOfSchedule, LocalDate datum, Long archivistId) {
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(idOfSchedule);
        Optional<Archivist> archivistOptional = archivistRepository.findById(archivistId);

        scheduleOptional.get().setAssignmentDay(datum);
        scheduleOptional.get().setArchivist(archivistOptional.get());

        scheduleRepository.save(scheduleOptional.get());
    }

    @Override
    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> scheduleList = scheduleRepository.findAll();

        return scheduleList.stream()
                .map(schedule -> new ScheduleDTO(
                        schedule.getId(),
                        schedule.getBroadcast(),
                        schedule.getChanel().getId(),
                        schedule.getChanel().getName(),
                        schedule.getAssignmentDay(),
                        schedule.getArchivist().getId(),
                        schedule.getArchivist().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public List<String> checkTheErrors(ModifyScheduleDTO modifyScheduleDTO) {
        List<String> errors = new ArrayList<>();

        Optional<Schedule> scheduleOptional = findById(modifyScheduleDTO.getId());
        if(scheduleOptional.isEmpty()){
            errors.add("nincs ilyen schedule");
        }

        Optional<Channel> channelOptional = chanelRepository.findById(modifyScheduleDTO.getChanelId());
        if(channelOptional.isEmpty()){
            errors.add("nincs ilyen id-val rendelkező csatorna");
        }

        Optional<Archivist> archivistOptional = archivistRepository.findById(modifyScheduleDTO.getArchivistId());
        if(archivistOptional.isEmpty()){
            errors.add("nincs ilyen id-val rendelkező archivátor");
        }

        if(modifyScheduleDTO.getBroadcast().equals(modifyScheduleDTO.getAssignmentDay())){
            errors.add("nem lehet a csatorna sugárzás napjával megeggyező időpontot választani");
        }

        if(modifyScheduleDTO.getAssignmentDay().isBefore(modifyScheduleDTO.getBroadcast())){
            errors.add("csatornát megelőző adásnapra napra nem lehet beosztani az archiválást");
        }

        return errors;
    }

    @Override
    public List<String> nullCheckDto(ModifyScheduleDTO modifyScheduleDTO) {
        List<String> error = new ArrayList<>();

        if(modifyScheduleDTO.getId() == null){
            error.add("schedule id");
        }
        if(modifyScheduleDTO.getBroadcast() == null){
            error.add("csatorna sugárzás napja");
        }
        if(modifyScheduleDTO.getChanelId() == null){
            error.add("csatorna id");
        }
        if(modifyScheduleDTO.getAssignmentDay() == null){
            error.add("kiosztás napja");
        }
        if(modifyScheduleDTO.getArchivistId() == null){
            error.add("archivátor id");
        }

        return error;
    }

    @Override
    public List<ScheduleDTO> getSchedulesByDate(LocalDate currentMonday) {
        LocalDate nextFriday = currentMonday.plusDays(4);
        List<Schedule> schedules = scheduleRepository.findByAssignmentDayBetween(currentMonday, nextFriday);

        return schedules.stream()
                .map(schedule -> new ScheduleDTO(
                        schedule.getId(),
                        schedule.getBroadcast(),
                        schedule.getChanel().getId(),
                        schedule.getChanel().getName(),
                        schedule.getAssignmentDay(),
                        schedule.getArchivist().getId(),
                        schedule.getArchivist().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getSchedulesByDateAndArchivistId(LocalDate currentMonday, Long id) {
        LocalDate nextFriday = currentMonday.plusDays(4);
        List<Schedule> schedules = scheduleRepository.findByAssignmentDayBetween(currentMonday, nextFriday);

        return schedules.stream()
                .filter(schedule -> schedule.getArchivist().getId().equals(id))
                .map(schedule -> new ScheduleDTO(
                        schedule.getId(),
                        schedule.getBroadcast(),
                        schedule.getChanel().getId(),
                        schedule.getChanel().getName(),
                        schedule.getAssignmentDay(),
                        schedule.getArchivist().getId(),
                        schedule.getArchivist().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> checkDayAndArchivist(DayAndArchivistIdDTO dayAndArchivistIdDTO) {
        List<String> error = new ArrayList<>();

        if(dayAndArchivistIdDTO.getDate() == null){
            error.add("hiányzó dátum");
        }

        if(dayAndArchivistIdDTO.getArchivistId() == null){
            error.add("hiányzó archivátor id");
        }

        Optional<Archivist> archivistOptional = archivistRepository.findById(dayAndArchivistIdDTO.getArchivistId());
        if(archivistOptional.isEmpty()){
            error.add("nem létező archivátor id");
        }

        return error;
    }

}
