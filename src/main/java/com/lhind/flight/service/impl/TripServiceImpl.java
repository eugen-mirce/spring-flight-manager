package com.lhind.flight.service.impl;

import com.lhind.flight.exception.TripServiceException;
import com.lhind.flight.model.entity.FlightEntity;
import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.model.entity.UserEntity;
import com.lhind.flight.repository.FlightRepository;
import com.lhind.flight.repository.TripRepository;
import com.lhind.flight.service.TripService;
import com.lhind.flight.shared.Constants;
import com.lhind.flight.shared.dto.FlightDTO;
import com.lhind.flight.shared.dto.TripDTO;
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
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final FlightRepository flightRepository;
    private final ModelMapper modelMapper;

    static final Logger logger = Logger.getLogger(TripServiceImpl.class);

    @Autowired
    public TripServiceImpl(TripRepository tripRepository, FlightRepository flightRepository, ModelMapper modelMapper) {
        this.tripRepository = tripRepository;
        this.flightRepository = flightRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TripDTO> getTrips(String status) {
        List<TripEntity> trips;
        if(Constants.ARRAY_STATUS.contains(status.toUpperCase())) {
            trips = tripRepository.findAllByStatus(status);
        } else {
            trips = tripRepository.findAll();
        }
        logger.info("Service: Fetching All Trips");
        Type listType = new TypeToken<List<TripDTO>>(){}.getType();
        return modelMapper.map(trips,listType);
    }

    @Override
    public Page<TripDTO> getTrips(String status, int page, int limit) {
        if(page > 0) page--;
        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<TripEntity> tripsPage;
        if(Constants.ARRAY_STATUS.contains(status.toUpperCase())) {
            tripsPage = tripRepository.findAllByStatus(status,pageableRequest);
        } else {
            tripsPage = tripRepository.findAll(pageableRequest);
        }
        logger.info("Service: Fetching All Trips");
        Type listType = new TypeToken<Page<TripDTO>>(){}.getType();
        return modelMapper.map(tripsPage,listType);
    }

    @Override
    public TripDTO getTrip(int tripId) {
        TripEntity trip = tripRepository.findById(tripId);
        if(trip == null) {
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));
        }
        logger.info("Service: Fetching Trip With ID: "+tripId);
        return modelMapper.map(trip,TripDTO.class);
    }

    @Override
    public TripDTO updateTrip(int tripId, TripDTO trip) {
        TripEntity tripEntity = tripRepository.findById(tripId);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.", new TripServiceException("Trip Not Found."));

        if(trip.getReason() == null || trip.getReason().isEmpty())
            logger.error("Service: Missing Field.", new TripServiceException("Missing field."));
        if(trip.getDescription() == null || trip.getDescription().isEmpty())
            logger.error("Service: Missing Field.", new TripServiceException("Missing field."));
        if(trip.getFrom() == null || trip.getFrom().isEmpty())
            logger.error("Service: Missing Field.", new TripServiceException("Missing field."));
        if(trip.getTo() == null || trip.getTo().isEmpty())
            logger.error("Service: Missing Field.", new TripServiceException("Missing field."));
        if(trip.getDepartureDate() == null)
            logger.error("Service: Missing Field.", new TripServiceException("Missing field."));
        if(trip.getArrivalDate() == null)
            logger.error("Service: Missing Field.", new TripServiceException("Missing field."));
        if(trip.getDepartureDate().before(new Date()))
            logger.error("Service: Departure date should be a date in the future.", new TripServiceException("Departure date should be a date in the future."));
        if(trip.getArrivalDate().before(new Date()))
            logger.error("Service: Arrival date should be a date in the future.", new TripServiceException("Arrival date should be a date in the future."));

        tripEntity.setReason(trip.getReason());
        tripEntity.setDescription(trip.getDescription());
        tripEntity.setFrom(trip.getFrom());
        tripEntity.setTo(trip.getTo());
        tripEntity.setDepartureDate(trip.getDepartureDate());
        tripEntity.setArrivalDate(trip.getArrivalDate());
        tripEntity.setStatus(trip.getStatus());
        tripRepository.save(tripEntity);

        logger.info("Service: Updated Trip With ID: "+tripId);
        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public void deleteTrip(int tripId) {
        TripEntity tripEntity = tripRepository.findById(tripId);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        tripRepository.delete(tripEntity);
        logger.info("Service: Deleted Trip With ID: "+tripId);
    }

    @Override
    public void changeStatus(int tripId, String status) {
        TripEntity tripEntity = tripRepository.findById(tripId);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.", new TripServiceException("Trip Not Found."));
        if(Constants.ARRAY_STATUS.contains(status)) {
            tripEntity.setStatus(status);
            tripRepository.save(tripEntity);
        } else
            logger.error("Service: Status Not Valid.", new TripServiceException("Status Not Valid."));
    }

    @Override
    public List<FlightDTO> getFlights(int tripId) {
        TripEntity tripEntity = tripRepository.findById(tripId);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.", new TripServiceException("Trip Not Found."));

        List<FlightEntity> flights = flightRepository.findAllByTrip(tripEntity);
        Type listType = new TypeToken<List<FlightDTO>>(){}.getType();
        logger.info("Service: Fetching Flights For Trip With ID: "+tripId);
        return modelMapper.map(flights,listType);
    }

    @Override
    public Page<FlightDTO> getFlights(int tripId, int page, int limit) {
        TripEntity tripEntity = tripRepository.findById(tripId);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.", new TripServiceException("Trip Not Found."));

        if(page > 0) page--;
        Pageable pageableRequest = PageRequest.of(page,limit);

        Page<FlightEntity> flights = flightRepository.findAllByTrip(tripEntity, pageableRequest);
        Type listType = new TypeToken<Page<FlightDTO>>(){}.getType();
        return modelMapper.map(flights,listType);
    }
}
