package com.lhind.flight.repository;

import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, CriteriaBuilder.In> {
    List<TripEntity> findAllByUser(UserEntity userEntity);
    Optional<TripEntity> findByIdAndUser(int tripId, UserEntity userEntity);
}
