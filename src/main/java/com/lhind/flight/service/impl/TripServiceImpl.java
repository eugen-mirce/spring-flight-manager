package com.lhind.flight.service.impl;

import com.lhind.flight.exception.TripServiceException;
import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.repository.TripRepository;
import com.lhind.flight.service.TripService;
import com.lhind.flight.shared.Constants;
import com.lhind.flight.shared.dto.TripDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository, ModelMapper modelMapper) {
        this.tripRepository = tripRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TripDTO> getTrips(String status) {
        List<TripEntity> trips = new ArrayList<>();
        if(Constants.ARRAY_STATUS.contains(status.toUpperCase())) {
            trips = tripRepository.findAllByStatus(status);
        } else {
            trips = tripRepository.findAll();
        }
        Type listType = new TypeToken<List<TripDTO>>(){}.getType();
        return modelMapper.map(trips,listType);
    }

    @Override
    public TripDTO getTrip(int tripId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        return modelMapper.map(trip,TripDTO.class);
    }

    @Override
    public TripDTO updateTrip(int tripId, TripDTO trip) {
        TripEntity tripEntity = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        //TODO Add Exceptions For Missing Fields
        tripEntity.setReason(trip.getReason());
        tripEntity.setDescription(trip.getDescription());
        tripEntity.setFrom(trip.getFrom());
        tripEntity.setTo(trip.getTo());
        tripEntity.setDepartureDate(trip.getDepartureDate());
        tripEntity.setArrivalDate(trip.getArrivalDate());
        tripEntity.setStatus(trip.getStatus());
        tripRepository.save(tripEntity);

        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public void deleteTrip(int tripId) {
        TripEntity tripEntity = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        tripRepository.delete(tripEntity);
    }

    @Override
    public void changeStatus(int tripId, String status) {
        TripEntity tripEntity = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));
        if(Constants.ARRAY_STATUS.contains(status)) {
            tripEntity.setStatus(status);
            tripRepository.save(tripEntity);
        } else {
            throw new TripServiceException("Status Not Valid.");
        }
    }
}
