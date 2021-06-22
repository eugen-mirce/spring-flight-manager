package com.lhind.flight.controller;

import com.lhind.flight.model.response.FlightRest;
import com.lhind.flight.service.FlightService;
import com.lhind.flight.shared.dto.FlightDTO;
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

@RestController
@RequestMapping("/flights")
public class FlightController {
    private final FlightService flightService;
    private final ModelMapper modelMapper;

    @Autowired
    public FlightController(FlightService flightService, ModelMapper modelMapper) {
        this.flightService = flightService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<FlightRest> getFlights() {
        List<FlightDTO> flights = flightService.getFlights();
        Type listType = new TypeToken<List<FlightRest>>(){}.getType();
        return modelMapper.map(flights,listType);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "{flightId}")
    public FlightRest getFlight(@PathVariable int flightId) {
        FlightDTO flight = flightService.getFlight(flightId);
        return modelMapper.map(flight,FlightRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "{flightId}")
    public FlightRest updateFlight(@PathVariable int flightId, @RequestBody FlightDTO flight) {
        FlightDTO updatedFlight = flightService.updateFlight(flightId,flight);
        return modelMapper.map(updatedFlight,FlightRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "{flightId}")
    public ResponseEntity<String> deleteFlight(@PathVariable int flightId) {
        flightService.deleteFlight(flightId);
        return new ResponseEntity<>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }
}
