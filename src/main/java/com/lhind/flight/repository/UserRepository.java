package com.lhind.flight.repository;

import com.lhind.flight.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository     // JpaRepository extends PagingAndSortingRepository (which extends CrudRepository)
public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    UserEntity findById(int id);
    UserEntity findByEmail(String email);
}
