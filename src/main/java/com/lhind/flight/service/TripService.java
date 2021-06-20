package com.lhind.flight.service;

import com.lhind.flight.shared.dto.TripDTO;

import java.util.List;

public interface TripService {
    List<TripDTO> getTrips(String status);
    TripDTO getTrip(int tripId);
    TripDTO updateTrip(int tripId, TripDTO trip);
    void deleteTrip(int tripId);
    void changeStatus(int tripId, String status);
}
