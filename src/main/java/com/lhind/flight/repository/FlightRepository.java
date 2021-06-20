package com.lhind.flight.repository;

import com.lhind.flight.model.entity.FlightEntity;
import com.lhind.flight.model.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, Integer> {
    List<FlightEntity> findAllByTrip(TripEntity tripEntity);
    Optional<FlightEntity> findByIdAndTrip(int flightId, TripEntity tripEntity);
}
