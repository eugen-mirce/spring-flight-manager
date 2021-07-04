package com.lhind.flight.controller;

import com.lhind.flight.InitialSetup;
import com.lhind.flight.model.response.FlightRest;
import com.lhind.flight.model.response.TripRest;
import com.lhind.flight.model.response.UserRest;
import com.lhind.flight.service.UserService;
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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDTO userDTO) {
        logger.info("Controller: Creating User");
        UserDTO savedUser = userService.createUser(userDTO);
        return modelMapper.map(savedUser,UserRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PutMapping(path = "{userId}",
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable int userId, @RequestBody UserDTO userDTO) {
        logger.info("Controller: Updating User With ID: "+userId);
        UserDTO updateUser = userService.updateUser(userId,userDTO);
        return modelMapper.map(updateUser,UserRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{userId}",
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable int userId) {
        logger.info("Controller: Fetching User With ID: "+userId);
        UserDTO user = userService.getUser(userId);
        return modelMapper.map(user,UserRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @DeleteMapping(path = "{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        logger.info("Controller: Deleting User With ID: "+userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }

    @PostAuthorize("hasRole('ADMIN')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<UserRest> getUsers(@RequestParam(value = "page",defaultValue = "1") int page,
                                   @RequestParam(value = "limit",defaultValue = "10") int limit) {
        logger.info("Controller: Fetching Users In Page "+page+" With Limit "+limit);
        Page<UserDTO> users = userService.getUsers(page, limit);
        List<UserDTO> listUsers = users.getContent();
        Type listType = new TypeToken<List<UserRest>>(){}.getType();

        List<UserRest> userRestList = modelMapper.map(listUsers, listType);
        return new PageImpl<>(userRestList, users.getPageable(), users.getTotalElements());
    }

    @PreAuthorize("#userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PostMapping(path = "{userId}/trips",
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public TripRest createTrip(@PathVariable int userId, @RequestBody TripDTO trip) {
        logger.info("Controller: Creating Trip For User: "+userId);
        TripDTO savedTrip = userService.createTrip(userId,trip);
        return modelMapper.map(savedTrip, TripRest.class);
    }

    @PreAuthorize("#userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PostMapping(path = "{userId}/trips/{tripId}/request_approval")
    public void requestApproval(@PathVariable int userId, @PathVariable int tripId) {
        logger.info("Controller: Requesting Trip Approval");
        userService.requestTripApproval(userId,tripId);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{userId}/trips", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<TripRest> getTrips(@PathVariable int userId,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Controller: Fetching Trips For User With ID: "+userId);
        Page<TripDTO> trips = userService.getUserTrips(userId,page,limit);
        Type listType = new TypeToken<List<TripRest>>(){}.getType();
        List<TripRest> tripRestList = modelMapper.map(trips.getContent(),listType);
        return new PageImpl<>(tripRestList, trips.getPageable(), trips.getTotalElements());
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{userId}/trips/{tripId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public TripRest getTrip(@PathVariable int userId, @PathVariable int tripId) {
        logger.info("Controller: Fetching Trip With ID: "+tripId);
        TripDTO trip = userService.getTrip(userId,tripId);
        return modelMapper.map(trip,TripRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PutMapping(path = "{userId}/trips/{tripId}",
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public TripRest updateTrip(@PathVariable int userId, @PathVariable int tripId, @RequestBody TripDTO trip) {
        logger.info("Controller: Updating Trip With ID: "+tripId);
        TripDTO updatedTrip = userService.updateTrip(userId,tripId,trip);
        return modelMapper.map(updatedTrip,TripRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @DeleteMapping(path = "{userId}/trips/{tripId}")
    public ResponseEntity<String> deleteTrip(@PathVariable int userId, @PathVariable int tripId) {
        logger.info("Controller: Deleting Trip With ID: "+tripId);
        userService.deleteTrip(userId,tripId);
        return new ResponseEntity<>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }

    @PreAuthorize("#userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PostMapping(path = "{userId}/trips/{tripId}/flights",
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public FlightRest createFlight(@PathVariable int userId, @PathVariable int tripId, @RequestBody FlightDTO flight) {
        logger.info("Controller: Creating Flight For Trip With ID: "+tripId);
        FlightDTO savedFlight = userService.createFlight(userId, tripId, flight);
        return modelMapper.map(savedFlight,FlightRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{userId}/trips/{tripId}/flights",
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public Page<FlightRest> getFlights(@PathVariable int userId,
                                       @PathVariable int tripId,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Controller: Fetching Flights For Trip With ID: "+tripId);
        Page<FlightDTO> flights = userService.getFlights(userId,tripId, page, limit);
        Type listType = new TypeToken<List<FlightRest>>(){}.getType();
        List<FlightRest> flightRests = modelMapper.map(flights.getContent(), listType);
        return new PageImpl<>(flightRests, flights.getPageable(), flights.getTotalElements());
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @GetMapping(path = "{userId}/trips/{tripId}/flights/{flightId}",
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public FlightRest getFlight(@PathVariable int userId, @PathVariable int tripId, @PathVariable int flightId) {
        logger.info("Controller: Fetching Flight With ID: "+flightId);
        FlightDTO flight = userService.getFlight(userId,tripId,flightId);
        return modelMapper.map(flight,FlightRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @PutMapping(path = "{userId}/trips/{tripId}/flights/{flightId}",
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public FlightRest updateFlight(@PathVariable int userId, @PathVariable int tripId, @PathVariable int flightId, @RequestBody FlightDTO flight) {
        logger.info("Controller: Updating Flight With ID: "+flightId);
        FlightDTO updatedFlight = userService.updateFlight(userId,tripId,flightId,flight);
        return modelMapper.map(updatedFlight,FlightRest.class);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Bearer JWT-Token", paramType = "header")
    })
    @DeleteMapping(path = "{userId}/trips/{tripId}/flights/{flightId}")
    public ResponseEntity<String> deleteFlight(@PathVariable int userId, @PathVariable int tripId, @PathVariable int flightId) {
        logger.info("Controller: Deleting Flight With ID: "+flightId);
        userService.deleteFlight(userId,tripId,flightId);
        return new ResponseEntity<>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }
}