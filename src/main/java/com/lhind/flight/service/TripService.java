package com.lhind.flight.service;

import com.lhind.flight.shared.dto.FlightDTO;
import com.lhind.flight.shared.dto.TripDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TripService {
    List<TripDTO> getTrips(String status);
    Page<TripDTO> getTrips(String status, int page, int limit);
    TripDTO getTrip(int tripId);
    TripDTO updateTrip(int tripId, TripDTO trip);
    void deleteTrip(int tripId);
    void changeStatus(int tripId, String status);

    List<FlightDTO> getFlights(int tripId);
    Page<FlightDTO> getFlights(int tripId, int page, int limit);
}
