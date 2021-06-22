package com.lhind.flight.repository;

import com.lhind.flight.model.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity,Integer> {
    RoleEntity findByName(String name);
}
