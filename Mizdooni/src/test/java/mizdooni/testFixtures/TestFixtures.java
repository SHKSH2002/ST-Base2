package mizdooni.testFixtures;

import mizdooni.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestFixtures {

    public static Address createSampleAddress() {
        return new Address("USA", "New York", "5th Avenue");
    }

    public static User createSampleUser() {
        return new User("john_doe", "password123", "john.doe@example.com",
                createSampleAddress(), User.Role.client);
    }

    public static User createSampleManager() {
        return new User("manager", "managerpass", "manager@example.com",
                createSampleAddress(), User.Role.manager);
    }

    public static Table createSampleTable(int tableNumber, int restaurantId) {
        return new Table(tableNumber, restaurantId, 4);
    }

    public static Restaurant createSampleRestaurant() {
        Restaurant restaurant = new Restaurant(
                "Gourmet Dine",
                createSampleManager(),
                "Fine Dining",
                LocalTime.of(12, 0),
                LocalTime.of(22, 0),
                "A place for exquisite meals",
                createSampleAddress(),
                "http://example.com/image.jpg"
        );
        restaurant.addTable(createSampleTable(1, restaurant.getId()));
        restaurant.addTable(createSampleTable(2, restaurant.getId()));
        return restaurant;
    }

    public static Rating createSampleRating() {
        Rating rating = new Rating();
        rating.food = 4.5;
        rating.service = 4.0;
        rating.ambiance = 5.0;
        rating.overall = 4.5;
        return rating;
    }

    public static Review createSampleReview(User user) {
        return new Review(
                user,
                createSampleRating(),
                "Excellent dining experience!",
                LocalDateTime.now().minusDays(1)
        );
    }

    public static Reservation createSampleReservation(User user, Restaurant restaurant, Table table) {
        return new Reservation(
                user,
                restaurant,
                table,
                LocalDateTime.now().plusDays(1)
        );
    }
}
