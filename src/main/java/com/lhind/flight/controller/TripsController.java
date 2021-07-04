package com.lhind.flight.controller;

import com.lhind.flight.model.response.FlightRest;
import com.lhind.flight.model.response.TripRest;
import com.lhind.flight.service.TripService;
import com.lhind.flight.shared.dto.FlightDTO;
import com.lhind.flight.shared.dto.TripDTO;
import com.lhind.flight.shared.dto.UserDTO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    static final Logger logger = Logger.getLogger(TripsController.class);
    @Autowired
    public TripsController(TripService tripService, ModelMapper modelMapper) {
        this.tripService = tripService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<TripRest> getTrips(@RequestParam(value = "status", defaultValue = "all") String status,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Controller: Fetching All Trips");
        Page<TripDTO> trips = tripService.getTrips(status, page, limit);
        List<TripDTO> listTrips = trips.getContent();
        Type listType = new TypeToken<List<TripRest>>(){}.getType();
        List<TripRest> tripRestList = modelMapper.map(listTrips, listType);

        return new PageImpl<>(tripRestList, trips.getPageable(), trips.getTotalElements());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{tripId}",
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public TripRest getTrip(@PathVariable int tripId) {
        logger.info("Controller: Fetching Trip With ID: "+tripId);
        TripDTO trip = tripService.getTrip(tripId);
        return modelMapper.map(trip,TripRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PutMapping(path = "{tripId}",
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public TripRest updateTrip(@PathVariable int tripId, @RequestBody TripDTO trip) {
        logger.info("Controller: Updating Trip With ID: "+tripId);
        TripDTO updatedTrip = tripService.updateTrip(tripId, trip);
        return modelMapper.map(updatedTrip,TripRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @DeleteMapping(path = "{tripId}")
    public ResponseEntity<String> deleteTrip(@PathVariable int tripId) {
        logger.info("Controller: Deleting Trip With ID: "+tripId);
        tripService.deleteTrip(tripId);
        return new ResponseEntity<>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PutMapping(path = "{tripId}/status/{status}")
    public void changeStatus(@PathVariable int tripId, @PathVariable String status) {
        logger.info("Controller: Changing Trip Status");
        status = status.toUpperCase();
        tripService.changeStatus(tripId,status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{tripId}/flights",
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<FlightRest> getFlights(@PathVariable int tripId,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Controller: Fetching Flights For Trip With ID: "+tripId);
        Page<FlightDTO> flights = tripService.getFlights(tripId,page,limit);
        Type listType = new TypeToken<List<FlightRest>>(){}.getType();
        List<FlightRest> flightRests = modelMapper.map(flights.getContent(),listType);
        return new PageImpl<>(flightRests, flights.getPageable(), flights.getTotalElements());
    }
}
