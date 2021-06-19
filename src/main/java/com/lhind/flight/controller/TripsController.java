package com.lhind.flight.controller;

import com.lhind.flight.model.response.TripRest;
import com.lhind.flight.service.TripService;
import com.lhind.flight.shared.dto.TripDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripsController {
    private final TripService tripService;
    private final ModelMapper modelMapper;

    @Autowired
    public TripsController(TripService tripService, ModelMapper modelMapper) {
        this.tripService = tripService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<TripRest> getTrips() {
        List<TripDTO> trips = tripService.getTrips();
        Type listType = new TypeToken<List<TripRest>>(){}.getType();
        return modelMapper.map(trips,listType);
    }
}
