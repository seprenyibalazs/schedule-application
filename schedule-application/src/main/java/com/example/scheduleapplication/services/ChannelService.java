package com.example.scheduleapplication.services;

import com.example.scheduleapplication.dtos.ChannelDTO;
import com.example.scheduleapplication.models.Channel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChannelService {
    List<Channel> findAll();

    List<Channel> findThoseDayWhatNotAccepted(LocalDate date);

    Optional<Channel> findById(Long channelId);

    List<ChannelDTO> getAllChannels();
}
