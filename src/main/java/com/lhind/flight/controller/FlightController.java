package com.lhind.flight.controller;

import com.lhind.flight.model.response.FlightRest;
import com.lhind.flight.service.FlightService;
import com.lhind.flight.shared.dto.FlightDTO;
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

@RestController
@RequestMapping("/flights")
public class FlightController {
    private final FlightService flightService;
    private final ModelMapper modelMapper;

    static final Logger logger = Logger.getLogger(FlightController.class);

    @Autowired
    public FlightController(FlightService flightService, ModelMapper modelMapper) {
        this.flightService = flightService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<FlightRest> getFlights(@RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Controller: Fetching All Flights");
        Page<FlightDTO> flights = flightService.getFlights(page,limit);
        Type listType = new TypeToken<List<FlightRest>>(){}.getType();
        List<FlightRest> flightRests = modelMapper.map(flights.getContent(), listType);
        return new PageImpl<>(flightRests, flights.getPageable(), flights.getTotalElements());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{flightId}",
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public FlightRest getFlight(@PathVariable int flightId) {
        logger.info("Controller: Fetching Flight With ID: "+flightId);
        FlightDTO flight = flightService.getFlight(flightId);
        return modelMapper.map(flight,FlightRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PutMapping(path = "{flightId}",
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public FlightRest updateFlight(@PathVariable int flightId, @RequestBody FlightDTO flight) {
        logger.info("Controller: Updating Flight With ID: "+flightId);
        FlightDTO updatedFlight = flightService.updateFlight(flightId,flight);
        return modelMapper.map(updatedFlight,FlightRest.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @DeleteMapping(path = "{flightId}")
    public ResponseEntity<String> deleteFlight(@PathVariable int flightId) {
        logger.info("Controller: Deleting Flight With ID: "+flightId);
        flightService.deleteFlight(flightId);
        return new ResponseEntity<>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }
}
