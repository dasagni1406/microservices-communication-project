package com.eca.hotel.service.Hotels.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eca.hotel.service.Hotels.entities.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {
    
}
