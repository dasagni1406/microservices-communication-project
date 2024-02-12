package com.eca.hotel.service.Hotels.services;

import java.util.List;
import com.eca.hotel.service.Hotels.entities.Hotel;

public interface HotelService {
    Hotel createHotel(Hotel hotel);
    Hotel getHotel(String hotelId);
    List<Hotel> getAllHotels();
}
