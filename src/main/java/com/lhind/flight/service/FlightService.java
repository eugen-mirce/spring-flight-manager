package com.lhind.flight.service;

import com.lhind.flight.shared.dto.FlightDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FlightService {
    List<FlightDTO> getFlights();
    Page<FlightDTO> getFlights(int page, int limit);
    FlightDTO getFlight(int flightId);
    FlightDTO updateFlight(int flightId, FlightDTO flight);
    void deleteFlight(int flightId);
}
