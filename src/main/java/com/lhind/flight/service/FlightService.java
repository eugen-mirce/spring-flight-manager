package com.lhind.flight.service;

import com.lhind.flight.shared.dto.FlightDTO;

import java.util.List;

public interface FlightService {
    List<FlightDTO> getFlights();
    FlightDTO getFlight(int flightId);
    FlightDTO updateFlight(int flightId, FlightDTO flight);
    void deleteFlight(int flightId);
}
