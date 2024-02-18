package com.eca.user.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.annotation.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.eca.user.service.Users.entities.Hotel;
import com.eca.user.service.Users.entities.Rating;
import com.eca.user.service.Users.entities.User;
import com.eca.user.service.Users.exceptions.ResourceNotFoundException;
import com.eca.user.service.Users.repositories.UserRepository;
import com.eca.user.service.Users.services.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceApplicationTests {

	private MockMvc mockMvc;

	ObjectMapper objectMapper = new ObjectMapper();
	ObjectWriter objectWriter = objectMapper.writer();

	@Mock
	private UserRepository userRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	User userOne = new User("u1", "nameOne", "emailOne", "aboutOne", null);
	User userTwo = new User("u2", "nameTwo", "emailTwo", "aboutTwo", null);

	Hotel hotelOne = new Hotel("h1", "hotelOne", "locOne", "aboutHotelOne");
	Hotel hotelTwo = new Hotel("h2", "hotelTwo", "locTwo", "aboutHotelTwo");

	Rating ratingOne = new Rating("r1", "u1", "h1", 10, "feedbackOne", null);
	Rating ratingTwo = new Rating("r2", "u2", "h2", 8, "feedbackTwo", null);

	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(userServiceImpl).build();
	}

	@Test
	public void saveUser_success() throws Exception {

		Mockito.when(userRepository.save(userOne)).thenReturn(userOne);
		User savedUser = userServiceImpl.saveUser(userOne);

		Assert.assertNotNull(savedUser);
		Assert.assertEquals(userOne.getName(), savedUser.getName());
		Assert.assertEquals(userOne.getEmail(), savedUser.getEmail());
		Assert.assertEquals(userOne.getAbout(), savedUser.getAbout());
		Assert.assertTrue(
				savedUser.getUserId().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
		Mockito.verify(userRepository).save(userOne);
	}

	@Test
	public void getAllUsers_emptyList() {
		Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());
		List<User> users = userServiceImpl.getAllUsers();
		Assert.assertTrue(users.isEmpty());
	}

	@Test
	public void getAllUsers_success() {
		// Mock user repository
		List<User> mockUsers = Arrays.asList(userOne, userTwo);
		Mockito.when(userRepository.findAll()).thenReturn(mockUsers);

		// Mock rating service
		Rating[] arrayRatingOne = new Rating[] { ratingOne };
		Rating[] arrayRatingTwo = new Rating[] { ratingTwo };

		Mockito.when(restTemplate.getForObject("http://RATING-SERVICE/ratings/users/u1", Rating[].class))
				.thenReturn(arrayRatingOne);
		Mockito.when(restTemplate.getForObject("http://RATING-SERVICE/ratings/users/u2", Rating[].class))
				.thenReturn(arrayRatingTwo);

		Mockito.when(restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/h1", Hotel.class))
				.thenReturn(new ResponseEntity<>(hotelOne, HttpStatus.OK));
		Mockito.when(restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/h2", Hotel.class))
				.thenReturn(new ResponseEntity<>(hotelTwo, HttpStatus.OK));

		ratingOne.setHotel(hotelOne);
		List<Rating> ratingListOne = new ArrayList<>(Arrays.asList(ratingOne));
		userOne.setRatings(ratingListOne);

		ratingTwo.setHotel(hotelTwo);
		List<Rating> ratingListTwo = new ArrayList<>(Arrays.asList(ratingTwo));
		userOne.setRatings(ratingListTwo);

		List<User> users = userServiceImpl.getAllUsers();

		Assert.assertEquals(2, users.size());

		Assert.assertEquals(ratingListOne.get(0), users.get(0).getRatings().get(0));
		Assert.assertEquals(ratingListOne.get(0).getHotel(), users.get(0).getRatings().get(0).getHotel());

		Assert.assertEquals(ratingListTwo.get(0), users.get(1).getRatings().get(0));
		Assert.assertEquals(ratingListTwo.get(0).getHotel(), users.get(1).getRatings().get(0).getHotel());
	}

	@Test
	public void testGetUserWithRatingsAndHotels() {
		// Mock user repository
		Rating[] ratingArray = new Rating[] { ratingOne };
		Mockito.when(userRepository.findById("u1")).thenReturn(Optional.of(userOne));

		Mockito.when(restTemplate.getForObject("http://RATING-SERVICE/ratings/users/u1", Rating[].class))
				.thenReturn(ratingArray);

		Mockito.when(restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/h1", Hotel.class))
				.thenReturn(new ResponseEntity<>(hotelOne, HttpStatus.OK));

		ratingOne.setHotel(hotelOne);
		List<Rating> ratingList = new ArrayList<>(Arrays.asList(ratingOne));
		userOne.setRatings(ratingList);
		User user = userServiceImpl.getUser("u1");

		Assert.assertNotNull(user);
		Assert.assertEquals(ratingList.get(0), user.getRatings().get(0));
		Assert.assertEquals(ratingList.get(0).getHotel(), user.getRatings().get(0).getHotel());
	}

	@Test
	public void testGetUserNotFound() {
		Mockito.when(userRepository.findById("nonExistentId")).thenReturn(Optional.empty());
		try {
			userServiceImpl.getUser("nonExistentId");
			Assert.fail("Exception not thrown");
		} catch (ResourceNotFoundException e) {
			Assert.assertEquals("User with given Id doesn't exist: nonExistentId", e.getMessage());
		}
	}
}
