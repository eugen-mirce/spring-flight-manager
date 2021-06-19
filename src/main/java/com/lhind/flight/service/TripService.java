package com.lhind.flight.service;

import com.lhind.flight.shared.dto.TripDTO;

import java.util.List;

public interface TripService {
    List<TripDTO> getTrips();
}
