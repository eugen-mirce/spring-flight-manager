package com.lhind.flight.controller;

import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.model.response.FlightRest;
import com.lhind.flight.model.response.TripRest;
import com.lhind.flight.model.response.UserRest;
import com.lhind.flight.service.UserService;
import com.lhind.flight.shared.dto.FlightDTO;
import com.lhind.flight.shared.dto.TripDTO;
import com.lhind.flight.shared.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    //TODO Add Admin Access Only
    @PostMapping
    public UserRest createUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.createUser(userDTO);
        return modelMapper.map(savedUser,UserRest.class);
    }

    //TODO Authenticated Only And Admin Or Specified User Only
    @PutMapping(path = "{userId}")
    public UserRest updateUser(@PathVariable int userId, @RequestBody UserDTO userDTO) {
        UserDTO updateUser = userService.updateUser(userId,userDTO);
        return modelMapper.map(updateUser,UserRest.class);
    }

    //TODO Authenticated Only And Admin Or Specified User Only
    @GetMapping(path = "{userId}")
    public UserRest getUser(@PathVariable int userId) {
        UserDTO user = userService.getUser(userId);
        return modelMapper.map(user,UserRest.class);
    }

    //TODO Authenticated Only And Admin Or Specified User Only
    @DeleteMapping(path = "{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<String>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }

    //TODO Authenticated Only And Admin
    @GetMapping
    public Page<UserRest> getUsers(@RequestParam(value = "page",defaultValue = "1") int page,
                                   @RequestParam(value = "limit",defaultValue = "10") int limit) {
        Page<UserDTO> users = userService.getUsers(page, limit);
        Type listType = new TypeToken<Page<UserRest>>(){}.getType();
        return modelMapper.map(users,listType);
    }

    //TODO Authenticated
    @PostMapping(path = "{userId}/trips")
    public TripRest createTrip(@PathVariable int userId, @RequestBody TripDTO trip) {
        TripDTO savedTrip = userService.createTrip(userId,trip);
        return modelMapper.map(savedTrip, TripRest.class);
    }

    //TODO Authenticated
    @PostMapping(path = "{userId}/trips/{tripId}/request_approval")
    public void requestApproval(@PathVariable int userId, @PathVariable int tripId) {
        userService.requestTripApproval(userId,tripId);
    }

    //TODO Authenticated
    @GetMapping(path = "{userId}/trips")
    public List<TripRest> getTrips(@PathVariable int userId) {
        List<TripDTO> trips = userService.getUserTrips(userId);
        Type listType = new TypeToken<List<TripRest>>(){}.getType();
        return modelMapper.map(trips,listType);
    }

    //TODO Authenticated
    @GetMapping(path = "{userId}/trips/{tripId}")
    public TripRest getTrip(@PathVariable int userId, @PathVariable int tripId) {
        TripDTO trip = userService.getTrip(userId,tripId);
        return modelMapper.map(trip,TripRest.class);
    }

    //TODO Authenticated
    @PutMapping(path = "{userId}/trips/{tripId}")
    public TripRest updateTrip(@PathVariable int userId, @PathVariable int tripId, @RequestBody TripDTO trip) {
        TripDTO updatedTrip = userService.updateTrip(userId,tripId,trip);
        return modelMapper.map(updatedTrip,TripRest.class);
    }

    //TODO Authenticated
    @DeleteMapping(path = "{userId}/trips/{tripId}")
    public ResponseEntity<String> deleteTrip(@PathVariable int userId, @PathVariable int tripId) {
        userService.deleteTrip(userId,tripId);
        return new ResponseEntity<String>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }

    //TODO Authenticated
    @PostMapping(path = "{userId}/trips/{tripId}/flights")
    public FlightRest createFlight(@PathVariable int userId, @PathVariable int tripId, @RequestBody FlightDTO flight) {
        FlightDTO savedFlight = userService.createFlight(userId, tripId, flight);

        return modelMapper.map(savedFlight,FlightRest.class);
    }

    //TODO Authenticated
    @GetMapping(path = "{userId}/trips/{tripId}/flights")
    public List<FlightRest> getFlights(@PathVariable int userId, @PathVariable int tripId) {
        List<FlightDTO> flights = userService.getFlights(userId,tripId);
        Type listType = new TypeToken<List<FlightRest>>(){}.getType();
        return modelMapper.map(flights,listType);
    }

    //TODO Authenticated
    @GetMapping(path = "{userId}/trips/{tripId}/flights/{flightId}")
    public FlightRest getFlight(@PathVariable int userId, @PathVariable int tripId, @PathVariable int flightId) {
        FlightDTO flight = userService.getFlight(userId,tripId,flightId);
        return modelMapper.map(flight,FlightRest.class);
    }

    //TODO Authenticated
    @PutMapping(path = "{userId}/trips/{tripId}/flights/{flightId}")
    public FlightRest updateFlight(@PathVariable int userId, @PathVariable int tripId, @PathVariable int flightId, @RequestBody FlightDTO flight) {
        FlightDTO updatedFlight = userService.updateFlight(userId,tripId,flightId,flight);
        return modelMapper.map(updatedFlight,FlightRest.class);
    }

    //TODO Authenticated
    @DeleteMapping(path = "{userId}/trips/{tripId}/flights/{flightId}")
    public ResponseEntity<String> deleteFlight(@PathVariable int userId, @PathVariable int tripId, @PathVariable int flightId) {
        userService.deleteFlight(userId,tripId,flightId);
        return new ResponseEntity<String>("DELETED", new HttpHeaders(), HttpStatus.OK);
    }
}