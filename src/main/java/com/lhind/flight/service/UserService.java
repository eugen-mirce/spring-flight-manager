package com.lhind.flight.service;

import com.lhind.flight.shared.dto.FlightDTO;
import com.lhind.flight.shared.dto.TripDTO;
import com.lhind.flight.shared.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(int id, UserDTO userDTO);
    void deleteUser(int id);
    UserDTO getUser(int id);
    Page<UserDTO> getUsers(int page,int limit);

    TripDTO createTrip(int userId, TripDTO trip);   //??
    List<TripDTO> getUserTrips(int userId);

    FlightDTO createFlight(int userId, int tripId, FlightDTO flight);
}
