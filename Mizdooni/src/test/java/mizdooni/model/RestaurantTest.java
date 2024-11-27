package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private Restaurant restaurant;
    private Restaurant restaurant2;
    private User manager;
    private String type;
    private LocalTime startTime;
    private LocalTime endTime;
    private Address address;
    private String imageLink;
    private Table table1;
    private Table table2;
    private User user1;

    @BeforeEach
    void setUp() {
        manager = new User("username", "password", "email", address, User.Role.manager);
        type = "Italian";
        startTime = LocalTime.now();
        endTime = LocalTime.now();
        address = new Address("Iran", "tehran", "AmirAbad");
        imageLink = "image";
        table1 = new Table(1, 1, 4);
        table2 = new Table(2, 1, 3);
        user1 = new User("username1", "password", "email1", address, User.Role.client);
        restaurant = new Restaurant("restaurant1", manager, type, startTime, endTime, "", address, imageLink);
        restaurant.addTable(table1);
        restaurant.addTable(table2);
        restaurant2 = new Restaurant("restaurant2", manager, type, startTime, endTime, "", address, imageLink);
    }

    @Test
    void testGetTable(){
        Table result = restaurant.getTable(1);
        assertEquals(table1, result);
    }

    @Test
    void testGetTableWrongNumber(){
        Table result = restaurant.getTable(50000);
        assertEquals(null, result);
    }

    @Test
    void testAddTableSize(){
        Table table3 = new Table(3, 1, 2);
        restaurant.addTable(table3);
        assertEquals(3, restaurant.getTables().size());
    }

    @Test
    void testAddReview(){
        User user2 = new User("username2", "password", "email2", address, User.Role.client);
        Rating rating2 = new Rating();
        rating2.overall = 3;
        rating2.ambiance = 2;
        rating2.food = 4;
        rating2.service = 3;
        LocalDateTime time1 = LocalDateTime.of(2024,12,5,12,0);
        Review review2 = new Review(user2, rating2, "normal", time1);
        restaurant.addReview(review2);
        assertEquals(1, restaurant.getReviews().size());
    }

    @Test
    void testAddReviewWithSameUser(){
        Rating rating1 = new Rating();
        rating1.overall = 4;
        rating1.ambiance = 3;
        rating1.food = 2;
        rating1.service = 5;
        LocalDateTime rating1Time = LocalDateTime.of(2022,12,5,10,0);
        Review review1 = new Review(user1, rating1, "Good", rating1Time);
        Rating rating2 = new Rating();
        rating2.overall = 3;
        rating2.ambiance = 2;
        rating2.food = 4;
        rating2.service = 3;
        LocalDateTime time1 = LocalDateTime.of(2024,12,5,12,0);
        Review review2 = new Review(user1, rating2, "normal", time1);
        restaurant.addReview(review1);
        restaurant.addReview(review2);
        assertEquals(1, restaurant.getReviews().size());
    }

    @Test
    void testGetAverageRatingEmptyRating(){
        Rating result = restaurant.getAverageRating();
        assertEquals(0, result.overall);
        assertEquals(0, result.food);
        assertEquals(0, result.ambiance);
        assertEquals(0, result.service);
    }

    @Test
    void testGetAverageRatingOneRating(){
        Rating rating1 = new Rating();
        rating1.overall = 4;
        rating1.ambiance = 3;
        rating1.food = 2;
        rating1.service = 5;
        LocalDateTime rating1Time = LocalDateTime.of(2022,12,5,10,0);
        Review review1 = new Review(user1, rating1, "Good", rating1Time);
        restaurant.addReview(review1);
        Rating result = restaurant.getAverageRating();
        assertEquals(4, result.overall);
        assertEquals(2, result.food);
        assertEquals(3, result.ambiance);
        assertEquals(5, result.service);
    }

    @Test
    void testGetAverageRatingTwoRating(){
        Rating rating1 = new Rating();
        rating1.overall = 4;
        rating1.ambiance = 3;
        rating1.food = 2;
        rating1.service = 5;
        LocalDateTime rating1Time = LocalDateTime.of(2022,12,5,10,0);
        Review review1 = new Review(user1, rating1, "Good", rating1Time);
        Rating rating2 = new Rating();
        rating2.overall = 3;
        rating2.ambiance = 2;
        rating2.food = 4;
        rating2.service = 3;
        LocalDateTime time1 = LocalDateTime.of(2024,12,5,12,0);
        User user2 = new User("username2", "password", "email2", address, User.Role.client);
        Review review2 = new Review(user2, rating2, "normal", time1);
        restaurant.addReview(review1);
        restaurant.addReview(review2);

        Rating result = restaurant.getAverageRating();
        assertEquals(3.5, result.overall);
        assertEquals(3, result.food);
        assertEquals(2.5, result.ambiance);
        assertEquals(4, result.service);
    }

    @Test
    void testGetstarEmptyReview(){
        int result = restaurant.getStarCount();
        assertEquals(0,result);
    }

    @Test
    void testGetstarWithOneReview(){
        Rating rating1 = new Rating();
        rating1.overall = 4;
        LocalDateTime rating1Time = LocalDateTime.of(2022,12,5,10,0);
        Review review1 = new Review(user1, rating1, "Good", rating1Time);
        restaurant.addReview(review1);

        int result = restaurant.getStarCount();
        assertEquals(4, result);
    }

    @Test
    void testGetstarWithTwoReview(){
        Rating rating1 = new Rating();
        rating1.overall = 4;
        LocalDateTime rating1Time = LocalDateTime.of(2022,12,5,10,0);
        Review review1 = new Review(user1, rating1, "Good", rating1Time);
        Rating rating2 = new Rating();
        rating2.overall = 3;
        LocalDateTime time1 = LocalDateTime.of(2024,12,5,12,0);
        User user2 = new User("username2", "password", "email2", address, User.Role.client);
        Review review2 = new Review(user2, rating2, "normal", time1);
        restaurant.addReview(review1);
        restaurant.addReview(review2);

        int result = restaurant.getStarCount();
        assertEquals(4, result);
    }

    @Test
    void testGetstarWithThreeReview(){
        Rating rating1 = new Rating();
        rating1.overall = 4;
        LocalDateTime rating1Time = LocalDateTime.of(2022,12,5,10,0);
        Review review1 = new Review(user1, rating1, "Good", rating1Time);

        Rating rating2 = new Rating();
        rating2.overall = 3;
        LocalDateTime time1 = LocalDateTime.of(2024,12,5,12,0);
        User user2 = new User("username2", "password", "email2", address, User.Role.client);
        Review review2 = new Review(user2, rating2, "normal", time1);

        Rating rating3 = new Rating();
        rating3.overall = 3;
        LocalDateTime time3 = LocalDateTime.of(2023,12,5,12,0);
        User user3 = new User("username3", "password", "email3", address, User.Role.client);
        Review review3 = new Review(user3, rating3, "normal", time3);
        restaurant.addReview(review1);
        restaurant.addReview(review2);
        restaurant.addReview(review3);

        int result = restaurant.getStarCount();
        assertEquals(3, result);
    }

    @Test
    void testGetMaxSeatsNumberWithoutTable() {
        assertEquals(0, restaurant2.getMaxSeatsNumber());
    }

    @Test
    void testGetMaxSeatsNumberReturnsMaxTableNumbers() {
        assertEquals(4, restaurant.getMaxSeatsNumber());
    }
}
