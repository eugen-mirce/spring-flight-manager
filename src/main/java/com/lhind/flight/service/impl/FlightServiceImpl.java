package com.lhind.flight.service.impl;

import com.lhind.flight.exception.FlightServiceException;
import com.lhind.flight.model.entity.FlightEntity;
import com.lhind.flight.repository.FlightRepository;
import com.lhind.flight.service.FlightService;
import com.lhind.flight.shared.dto.FlightDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository, ModelMapper modelMapper) {
        this.flightRepository = flightRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<FlightDTO> getFlights() {
        List<FlightEntity> flights = flightRepository.findAll();
        Type listType = new TypeToken<List<FlightDTO>>(){}.getType();
        return modelMapper.map(flights,listType);
    }

    @Override
    public FlightDTO getFlight(int flightId) {
        FlightEntity flightEntity = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightServiceException("Flight Not Found."));

        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public FlightDTO updateFlight(int flightId, FlightDTO flight) {
        FlightEntity flightEntity = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightServiceException("Flight Not Found."));

        if(flight.getFrom() == null || flight.getFrom().isEmpty())
            throw new FlightServiceException("Missing field.");
        if(flight.getTo() == null || flight.getTo().isEmpty())
            throw new FlightServiceException("Missing field.");
        if(flight.getDepartureDate() == null)
            throw new FlightServiceException("Missing field.");
        if(flight.getArrivalDate() == null)
            throw new FlightServiceException("Missing field.");
        if(flight.getDepartureDate().before(new Date()))
            throw new FlightServiceException("Departure date should be a date in the future.");
        if(flight.getArrivalDate().before(new Date()))
            throw new FlightServiceException("Arrival date should be a date in the future.");

        flightEntity.setFrom(flight.getFrom());
        flightEntity.setTo(flight.getTo());
        flightEntity.setDepartureDate(flight.getDepartureDate());
        flightEntity.setArrivalDate(flight.getArrivalDate());
        flightRepository.save(flightEntity);

        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public void deleteFlight(int flightId) {
        FlightEntity flightEntity = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightServiceException("Flight Not Found."));

        flightRepository.delete(flightEntity);
    }
}
