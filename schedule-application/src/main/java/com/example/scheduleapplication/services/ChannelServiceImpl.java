package com.example.scheduleapplication.services;

import com.example.scheduleapplication.dtos.ChannelDTO;
import com.example.scheduleapplication.models.Channel;
import com.example.scheduleapplication.models.Schedule;
import com.example.scheduleapplication.repositories.ChannelRepository;
import com.example.scheduleapplication.repositories.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ChannelServiceImpl implements ChannelService{

    private final ChannelRepository chanelRepository;
    private final ScheduleRepository scheduleRepository;
    @Override
    public List<Channel> findAll() {
        return chanelRepository.findAll();
    }

    @Override
    public List<Channel> findThoseDayWhatNotAccepted(LocalDate date) {
        List<Channel> channels = findAll();
        List<Schedule> schedules = scheduleRepository.findByBroadcast(date);

        List<Channel> filteredChannels = channels.stream()
                .filter(channel -> schedules.stream()
                        .noneMatch(schedule -> schedule.getChanel().getId().equals(channel.getId())))
                .collect(Collectors.toList());

        return filteredChannels;
    }

    @Override
    public Optional<Channel> findById(Long channelId) {
        return chanelRepository.findById(channelId);
    }

    @Override
    public List<ChannelDTO> getAllChannels() {
        List<Channel> channelList = chanelRepository.findAll();

        return channelList.stream()
                .map(channel -> new ChannelDTO(channel.getId(), channel.getName()))
                .collect(Collectors.toList());
    }

}
