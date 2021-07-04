package com.lhind.flight.service;

import com.lhind.flight.shared.dto.FlightDTO;
import com.lhind.flight.shared.dto.TripDTO;
import com.lhind.flight.shared.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(int id, UserDTO userDTO);
    void deleteUser(int id);
    UserDTO getUser(int id);
    UserDTO getUserByEmail(String email);
    Page<UserDTO> getUsers(int page,int limit);

    TripDTO createTrip(int userId, TripDTO trip);   //??
    List<TripDTO> getUserTrips(int userId);
    Page<TripDTO> getUserTrips(int userId,int page, int limit);
    TripDTO getTrip(int userId, int tripId);
    TripDTO updateTrip(int userId, int tripId, TripDTO trip);
    void requestTripApproval(int userId, int tripId);
    void deleteTrip(int userId, int tripId);

    FlightDTO createFlight(int userId, int tripId, FlightDTO flight);
    List<FlightDTO> getFlights(int userId, int tripId);
    Page<FlightDTO> getFlights(int userId, int tripId, int page, int limit);
    FlightDTO getFlight(int userId, int tripId, int flightId);
    FlightDTO updateFlight(int userId, int tripId, int flightId, FlightDTO flight);
    void deleteFlight(int userId, int tripId, int flightId);
}
