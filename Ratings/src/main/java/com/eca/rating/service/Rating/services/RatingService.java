package com.eca.rating.service.Rating.services;

import java.util.List;

import com.eca.rating.service.Rating.entities.Rating;

public interface RatingService {
    Rating createRating(Rating rating);
    List<Rating> getRatings();
    List<Rating> getRatingsByUserId(String userId);
    List<Rating> getRatingsByHotelId(String hotelId);
}
