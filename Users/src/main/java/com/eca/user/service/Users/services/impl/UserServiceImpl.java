package com.eca.user.service.Users.services.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.eca.user.service.Users.entities.Hotel;
import com.eca.user.service.Users.entities.Rating;
import com.eca.user.service.Users.entities.User;
import com.eca.user.service.Users.exceptions.ResourceNotFoundException;
import com.eca.user.service.Users.repositories.UserRepository;
import com.eca.user.service.Users.services.UserService;

@Service
public class UserServiceImpl implements UserService{

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public User saveUser(User user) {
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach((user) -> {
            Rating[] ratingsPerUserId = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" + user.getUserId(), Rating[].class);
            // Rating[] ratingsPerUserId = webClientBuilder.build().get().uri("http://RATING-SERVICE/ratings/users/" + user.getUserId()).retrieve().bodyToMono(Rating[].class).block();
            List<Rating> ratings = Arrays.asList(ratingsPerUserId);

            List<Rating> ratingsList = ratings.stream().map((r) -> {
                ResponseEntity<Hotel> hotel = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/" + r.getHotelId(), Hotel.class);
                r.setHotel(hotel.getBody());
                return r;
            }).collect(Collectors.toList());

            user.setRatings(ratingsList);
        });
        return users;
    }

    @Override
    public User getUser(String userId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User with given Id doesn't exist: " + userId));

        Rating[] ratingsPerUserId = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" + userId, Rating[].class);
        // Rating[] ratingsPerUserId = webClientBuilder.build().get().uri("http://RATING-SERVICE/ratings/users/" + userId).retrieve().bodyToMono(Rating[].class).block();
        List<Rating> ratings = Arrays.asList(ratingsPerUserId);

        List<Rating> ratingsList = ratings.stream().map((r) -> {
            ResponseEntity<Hotel> hotel = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/" + r.getHotelId(), Hotel.class);
            r.setHotel(hotel.getBody());
            return r;
        }).collect(Collectors.toList());

        user.setRatings(ratingsList);
        return user;
    }
    
}
