package com.example.aqisystem.repository;

import com.example.aqisystem.model.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {
    List<City> findAll(); // ensures we get a List instead of Iterable

    // helpful lookup by (case-insensitive) name
    Optional<City> findByNameIgnoreCase(String name);
}
