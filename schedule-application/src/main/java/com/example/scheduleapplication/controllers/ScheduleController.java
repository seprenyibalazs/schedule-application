package com.example.scheduleapplication.controllers;

import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.models.Schedule;
import com.example.scheduleapplication.services.ArchivistService;
import com.example.scheduleapplication.services.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ArchivistService archivistServices;

    @GetMapping("/naptar")
    public String getSchedule(@RequestParam(required = false) LocalDate date,
                              Model model) {
        LocalDate day = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        if (date != null) {
            day = date;
        }

        setDailyScheduleAttributes(day, model);

        return "naptar";
    }

    @PostMapping("/naptar")
    public String postSchedule(@RequestParam LocalDate datum,
                               @RequestParam(required = false) Long selectedArchivist,
                               Model model) {
        LocalDate localMonday = datum.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        if(selectedArchivist != 0 ){
            List<Archivist> archivistList = archivistServices.findAll();
            Optional<Archivist> archivistOptional = archivistServices.findById(selectedArchivist);

            List<Schedule> filteredMondaySchedule = scheduleService.findScheduleByDayAndArchivist(archivistOptional.get(), localMonday);
            List<Schedule> filteredTuesdaySchedule = scheduleService.findScheduleByDayAndArchivist(archivistOptional.get(), localMonday.plusDays(1));
            List<Schedule> filteredWednesdaySchedule = scheduleService.findScheduleByDayAndArchivist(archivistOptional.get(), localMonday.plusDays(2));
            List<Schedule> filteredThursdaySchedule = scheduleService.findScheduleByDayAndArchivist(archivistOptional.get(), localMonday.plusDays(3));
            List<Schedule> filteredFridaySchedule = scheduleService.findScheduleByDayAndArchivist(archivistOptional.get(), localMonday.plusDays(4));

            model.addAttribute("mondaysSchedule", filteredMondaySchedule);
            model.addAttribute("tuesdaysSchedule", filteredTuesdaySchedule);
            model.addAttribute("wednesdaysSchedule", filteredWednesdaySchedule);
            model.addAttribute("thursdaysSchedule", filteredThursdaySchedule);
            model.addAttribute("fridaysSchedule", filteredFridaySchedule);

            model.addAttribute("archivatorok", archivistList);
            model.addAttribute("scheduleStart", localMonday);
            model.addAttribute("scheduleEnd", localMonday.plusDays(4));

            return "naptar";
        }
        setDailyScheduleAttributes(localMonday, model);

        return "naptar";
    }

    private void setDailyScheduleAttributes(LocalDate day, Model model) {
        List<Archivist> archivistList = archivistServices.findAll();


        List<Schedule> mondayList = scheduleService.findScheduleByDay(day);
        List<Schedule> tuesdayList = scheduleService.findScheduleByDay(day.plusDays(1));
        List<Schedule> wednesdayList = scheduleService.findScheduleByDay(day.plusDays(2));
        List<Schedule> thursdayList = scheduleService.findScheduleByDay(day.plusDays(3));
        List<Schedule> fridayList = scheduleService.findScheduleByDay(day.plusDays(4));

        model.addAttribute("archivatorok", archivistList);

        model.addAttribute("mondaysSchedule", mondayList);
        model.addAttribute("tuesdaysSchedule", tuesdayList);
        model.addAttribute("wednesdaysSchedule", wednesdayList);
        model.addAttribute("thursdaysSchedule", thursdayList);
        model.addAttribute("fridaysSchedule", fridayList);

        model.addAttribute("scheduleStart", day);
        model.addAttribute("scheduleEnd", day.plusDays(4));
    }

    @PostMapping("/modify-schedule-day")
    public String modifyScheduleDay(@RequestParam Long scheduleId,
                                    Model model){
        model.addAttribute("idOfSchedule", scheduleId);

        return "modifyDay";
    }

    @PostMapping("/modify-schedule-archivist")
    public String modifyScheduleArchivist(@RequestParam Long idOfSchedule,
                                          @RequestParam LocalDate datum,
                                          Model model){

        if(datum == null){
            model.addAttribute("somethingwentwrong", true);
            model.addAttribute("missingStuff", "abc");
            return "error";
        }


        List<Archivist> archivistList = archivistServices.getThoseArchivistDontWorkThisDay(datum);

        model.addAttribute("archivatorok", archivistList);
        model.addAttribute("datum", datum);
        model.addAttribute("idOfSchedule", idOfSchedule);

        return "modifyArchivator";
    }

    @PostMapping("/modify-schedule")
    public String modifySchedule(@RequestParam Long idOfSchedule,
                                 @RequestParam LocalDate datum,
                                 @RequestParam Long archivistId,
                                 Model model){
        scheduleService.modifyTheSchedule(idOfSchedule, datum, archivistId);

        return "redirect:/naptar";
    }
}
