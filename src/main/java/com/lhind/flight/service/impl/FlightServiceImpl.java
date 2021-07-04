package com.lhind.flight.service.impl;

import com.lhind.flight.exception.FlightServiceException;
import com.lhind.flight.model.entity.FlightEntity;
import com.lhind.flight.repository.FlightRepository;
import com.lhind.flight.service.FlightService;
import com.lhind.flight.shared.dto.FlightDTO;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    private final ModelMapper modelMapper;

    static final Logger logger = Logger.getLogger(FlightServiceImpl.class);

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository, ModelMapper modelMapper) {
        this.flightRepository = flightRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<FlightDTO> getFlights() {
        logger.info("Service: Fetching All Flights");
        List<FlightEntity> flights = flightRepository.findAll();
        Type listType = new TypeToken<List<FlightDTO>>(){}.getType();
        return modelMapper.map(flights,listType);
    }

    @Override
    public Page<FlightDTO> getFlights(int page, int limit) {
        logger.info("Service: Fetching All Flights");
        if(page > 0) page--;
        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<FlightEntity> flights = flightRepository.findAll(pageableRequest);
        Type listType = new TypeToken<Page<FlightDTO>>(){}.getType();
        return modelMapper.map(flights, listType);
    }

    @Override
    public FlightDTO getFlight(int flightId) {

        FlightEntity flightEntity = flightRepository.findById(flightId);
        if(flightEntity == null)
            logger.error("Service: Flight With ID: "+flightId+" Not Found.",new FlightServiceException("Flight Not Found."));
        logger.info("Service: Fetching Flight With ID: "+flightId);
        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public FlightDTO updateFlight(int flightId, FlightDTO flight) {
        FlightEntity flightEntity = flightRepository.findById(flightId);
        if(flightEntity == null)
            logger.error("Service: Flight With ID: "+flightId+" Not Found.",new FlightServiceException("Flight Not Found."));
        if(flight.getFrom() == null || flight.getFrom().isEmpty())
            logger.error("Service: Required Field Is Missing",new FlightServiceException("Missing field."));
        if(flight.getTo() == null || flight.getTo().isEmpty())
            logger.error("Service: Required Field Is Missing",new FlightServiceException("Missing field."));
        if(flight.getDepartureDate() == null)
            logger.error("Service: Required Field Is Missing",new FlightServiceException("Missing field."));
        if(flight.getArrivalDate() == null)
            logger.error("Service: Required Field Is Missing",new FlightServiceException("Missing field."));
        if(flight.getDepartureDate().before(new Date()))
            logger.error("Service: Departure date should be a date in the future.",new FlightServiceException("Departure date should be a date in the future."));
        if(flight.getArrivalDate().before(new Date()))
            logger.error("Service: Arrival date should be a date in the future.",new FlightServiceException("Arrival date should be a date in the future."));

        flightEntity.setFrom(flight.getFrom());
        flightEntity.setTo(flight.getTo());
        flightEntity.setDepartureDate(flight.getDepartureDate());
        flightEntity.setArrivalDate(flight.getArrivalDate());
        flightRepository.save(flightEntity);
        logger.info("Service: Flight Updated Successfully.");
        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public void deleteFlight(int flightId) {
        FlightEntity flightEntity = flightRepository.findById(flightId);
        if(flightEntity == null)
            logger.error("Service: Flight With ID: "+flightId+" Not Found.",new FlightServiceException("Flight Not Found."));

        flightRepository.delete(flightEntity);
        logger.info("Service: Flight Deleted Successfully.");
    }
}
