package com.lhind.flight.repository;

import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Integer> {
    TripEntity findById(int tripId);
    TripEntity findByIdAndUser(int tripId, UserEntity userEntity);

    Page<TripEntity> findAll(Pageable pageableRequest);

    List<TripEntity> findAllByUser(UserEntity userEntity);
    Page<TripEntity> findAllByUser(UserEntity userEntity, Pageable pageableRequest);

    List<TripEntity> findAllByStatus(String status);
    Page<TripEntity> findAllByStatus(String status, Pageable pageableRequest);


}
