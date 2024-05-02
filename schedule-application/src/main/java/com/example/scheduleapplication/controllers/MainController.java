package com.example.scheduleapplication.controllers;

import com.example.scheduleapplication.models.Archivist;
import com.example.scheduleapplication.models.Channel;
import com.example.scheduleapplication.services.ArchivistService;
import com.example.scheduleapplication.services.ChannelService;
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
public class MainController {

    private final ChannelService channelService;
    private final ArchivistService archivistService;
    private final ScheduleService scheduleService;
    @GetMapping("/")
    public String getMain(@RequestParam(required = false) LocalDate date,
                          Model model){
        LocalDate currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        if(date != null){
            currentMonday = date;
        }

        setChannelsCalendarAttributes(currentMonday, model);

        return "index";
    }

    @PostMapping("/")
    public String modifyTheWeekOfModifyCalender(@RequestParam(required = false) LocalDate datum,
                                                Model model){
        if(datum == null){
            datum = LocalDate.now();
        }
        LocalDate currentMonday = datum.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        setChannelsCalendarAttributes(currentMonday, model);

        return "index";
    }

    private void setChannelsCalendarAttributes(LocalDate day, Model model){
        LocalDate currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<Channel> previousFridayList = channelService.findThoseDayWhatNotAccepted(day.minusDays(3));
        List<Channel> saturdayList = channelService.findThoseDayWhatNotAccepted(day.minusDays(2));
        List<Channel> sundayList = channelService.findThoseDayWhatNotAccepted(day.minusDays(1));
        List<Channel> mondayList = channelService.findThoseDayWhatNotAccepted(day);
        List<Channel> tuesdayList = channelService.findThoseDayWhatNotAccepted(day.plusDays(1));
        List<Channel> wednesdayList = channelService.findThoseDayWhatNotAccepted(day.plusDays(2));
        List<Channel> thursdayList = channelService.findThoseDayWhatNotAccepted(day.plusDays(3));


        model.addAttribute("prevFriday", day.minusDays(3));
        model.addAttribute("listOfPreviousFri", previousFridayList);
        model.addAttribute("saturday", day.minusDays(2));
        model.addAttribute("listOfSaturday", saturdayList);
        model.addAttribute("sunday", day.minusDays(1));
        model.addAttribute("listOfSunday", sundayList);
        model.addAttribute("monday", day);
        model.addAttribute("listOfMonday", mondayList);
        model.addAttribute("tuesday", day.plusDays(1));
        model.addAttribute("listOfTuesday", tuesdayList);
        model.addAttribute("wednesday", day.plusDays(2));
        model.addAttribute("listOfWednesday", wednesdayList);
        model.addAttribute("thursday", day.plusDays(3));
        model.addAttribute("listOfThursday", thursdayList);

        if(!day.minusDays(3).isBefore(currentMonday.minusDays(3))){
            model.addAttribute("checkTheModifiability", true);
        }

        model.addAttribute("weekStart", day.minusDays(3));
        model.addAttribute("weekEnd", day.plusDays(3));
    }
    @PostMapping("/achivator-kivalasztas")
    public String sendDates(@RequestParam(required = false) LocalDate datum,
                            @RequestParam Long channelId,
                            @RequestParam LocalDate broadcast,
                            @RequestParam LocalDate chosenMonday,
                            Model model){

        if(datum == null){
            model.addAttribute("somethingwentwrong", true);
            model.addAttribute("missingStuff", "Kérlek válassz egy dátumot az archiváláshoz!");
            model.addAttribute("chosenMonday", chosenMonday);
            return "error";
        }

        if(datum.equals(broadcast)) {
            model.addAttribute("somethingwentwrong", true);
            model.addAttribute("missingStuff", "Az archiválás nem eshet a sugárzással egy napra, kérlek válassz másik dátumot!");
            model.addAttribute("chosenMonday", chosenMonday);
            return "error";
        }

        if(datum.getDayOfWeek() == DayOfWeek.SATURDAY || datum.getDayOfWeek() == DayOfWeek.SUNDAY) {
            model.addAttribute("somethingwentwrong", true);
            model.addAttribute("missingStuff", "A kiválasztott dátum hétvégére esik, kérlek válassz másik dátumot!");
            model.addAttribute("chosenMonday", chosenMonday);
            return "error";
        }

        if(!datum.isAfter(broadcast)){
            model.addAttribute("somethingwentwrong", true);
            model.addAttribute("missingStuff", "Az archiválás nem eshet a sugárzást megelőző napra, kérlek válasszál másik dátumot!");
            model.addAttribute("chosenMonday", chosenMonday);
            return "error";
        }

        List<Archivist> archivistList = archivistService.getThoseArchivistDontWorkThisDay(datum);

        if(archivistList.isEmpty()){
            model.addAttribute("somethingwentwrong", true);
            model.addAttribute("missingStuff", datum + " ezen a napon nincs elérhető munkatárs, kérlek válassz egy másik napot!");
            model.addAttribute("chosenMonday", chosenMonday);
            return "error";
        }

        Optional<Channel> channel = channelService.findById(channelId);
        model.addAttribute("channelName", channel.get().getName());
        model.addAttribute("chosenDay", datum);
        model.addAttribute("channelId", channelId);
        model.addAttribute("broadcastDay", broadcast);

        model.addAttribute("archivists", archivistList);

        return "selectArchivist";
    }

    @PostMapping("/assign-archivist-to-channel")
    public String assignArchivistToChannel(@RequestParam(required = false) LocalDate datum,
                                           @RequestParam Long channelId,
                                           @RequestParam LocalDate broadcast,
                                           @RequestParam Long selectedArchivist){
        Optional<Archivist> archivistOptional = archivistService.findById(selectedArchivist);

        scheduleService.createSchedule(archivistOptional.get(), channelId, broadcast, datum);

        return "redirect:/";
    }
}
