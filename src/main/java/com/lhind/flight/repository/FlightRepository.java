package com.lhind.flight.repository;

import com.lhind.flight.model.entity.FlightEntity;
import com.lhind.flight.model.entity.TripEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, Integer> {
    FlightEntity findById(int flightId);
    FlightEntity findByIdAndTrip(int flightId, TripEntity tripEntity);

    Page<FlightEntity> findAll(Pageable pageableRequest);

    List<FlightEntity> findAllByTrip(TripEntity tripEntity);
    Page<FlightEntity> findAllByTrip(TripEntity tripEntity, Pageable pageableRequest);

}
