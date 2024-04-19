package com.example.scheduleapplication.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "archivist_id")
    private Archivist archivist;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Channel chanel;

    private LocalDate broadcast;

    private LocalDate assignmentDay;
}
