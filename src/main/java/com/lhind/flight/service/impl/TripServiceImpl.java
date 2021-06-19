package com.lhind.flight.service.impl;

import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.repository.TripRepository;
import com.lhind.flight.service.TripService;
import com.lhind.flight.shared.dto.TripDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository, ModelMapper modelMapper) {
        this.tripRepository = tripRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TripDTO> getTrips() {
        List<TripEntity> trips = tripRepository.findAll();
        Type listType = new TypeToken<List<TripDTO>>(){}.getType();
        return modelMapper.map(trips,listType);
    }
}
