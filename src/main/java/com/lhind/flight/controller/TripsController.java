package com.lhind.flight.controller;

import com.lhind.flight.model.response.TripRest;
import com.lhind.flight.service.TripService;
import com.lhind.flight.shared.dto.TripDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<TripRest> getTrips(@RequestParam(value = "status", defaultValue = "all") String status) {
        List<TripDTO> trips = tripService.getTrips(status);
        Type listType = new TypeToken<List<TripRest>>(){}.getType();
        return modelMapper.map(trips,listType);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "{tripId}")
    public TripRest getTrip(@PathVariable int tripId) {
        TripDTO trip = tripService.getTrip(tripId);
        return modelMapper.map(trip,TripRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "{tripId}")
    public TripRest updateTrip(@PathVariable int tripId, @RequestBody TripDTO trip) {
        TripDTO updatedTrip = tripService.updateTrip(tripId, trip);
        return modelMapper.map(updatedTrip,TripRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "{tripId}")
    public ResponseEntity<String> deleteTrip(@PathVariable int tripId) {
        tripService.deleteTrip(tripId);
        return new ResponseEntity<>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "{tripId}/status/{status}")
    public void changeStatus(@PathVariable int tripId, @PathVariable String status) {
        status = status.toUpperCase();
        tripService.changeStatus(tripId,status);
    }
}
