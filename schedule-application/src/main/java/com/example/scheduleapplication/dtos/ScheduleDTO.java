package com.example.scheduleapplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {
    private Long id;
    private LocalDate broadcast;
    private Long channelId;
    private String channelName;
    private LocalDate assignmentDay;
    private Long archivistId;
    private String archivistName;
}
