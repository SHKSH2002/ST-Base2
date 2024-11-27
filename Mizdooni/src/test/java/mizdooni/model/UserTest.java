package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private Table table;
    private User user;
    private User user2;
    private Address address;
    private Address address2;
    private Restaurant restaurant;
    private Restaurant restaurant2;
    private User managerUser;
    private Reservation reservation;
    private Reservation reservation2;
    private Reservation reservation3;

    @BeforeEach
    void setUp() {
        table = new Table(1, 1, 4);
        address = new Address("Country", "City", "123 Main St");
        address2 = new Address("Country2", "City2", "123 Main St2");
        user = new User("testUser", "password123", "test@example.com", address, User.Role.client);
        user2 = new User("testUser2", "password", "test2@example.com", address, User.Role.client);
        managerUser = new User("username2", "password2", "email2", address, User.Role.manager);
        restaurant = new Restaurant("name", managerUser, "type", LocalTime.of(9, 0, 0),
                LocalTime.of(22, 0, 0), "description", address2, "imageLink");
        restaurant2 = new Restaurant("name2", managerUser, "type", LocalTime.of(9, 0, 0),
                LocalTime.of(22, 0, 0), "description2", address2, "imageLink2");
        reservation = new Reservation(
                user, restaurant, table,
                LocalDateTime.of(2024, 10, 21, 18, 0)
        );
        reservation2 = new Reservation(
                user, restaurant, table,
                LocalDateTime.now().plusHours(2)
        );
        reservation3 = new Reservation(
                user2, restaurant, table,
                LocalDateTime.of(2024, 10, 21, 20, 0)
        );
        reservation3.cancel();
    }

    @Test
    void testAddReservationIncrementsCounter() {
        user.addReservation(reservation);
        user.addReservation(reservation2);
        user2.addReservation(reservation3);
        assertEquals(0, reservation.getReservationNumber());
        assertEquals(1, reservation2.getReservationNumber());
        assertEquals(0, reservation3.getReservationNumber());
    }

    @Test
    void testAddReservationAddToReservationList() {
        user.addReservation(reservation);
        user.addReservation(reservation2);
        assertEquals(2, user.getReservations().size());
    }

    @Test
    void testCheckReservedReturnsTrueForActiveReservation() {
        user.addReservation(reservation);
        assertTrue(user.checkReserved(reservation.getRestaurant()));
    }

    @Test
    void testCheckReservedReturnsFalseForFutureReservation() {
        user.addReservation(reservation2);
        assertFalse(user.checkReserved(reservation2.getRestaurant()));
    }

    @Test
    void testCheckReservedReturnsFalseForCancelledReservation() {
        user2.addReservation(reservation3);
        assertFalse(user2.checkReserved(reservation3.getRestaurant()));
    }

    @Test
    void testCheckReservedReturnsFalseForInvalidRestaurant() {
        user2.addReservation(reservation3);
        assertFalse(user2.checkReserved(restaurant2));
    }

    @Test
    void testGetReservationReturnsCorrectReservation() {
        user.addReservation(reservation2);
        assertEquals(reservation2, user.getReservation(0));
    }

    @Test
    void testGetReservationReturnsNullForCancelledReservation() {
        user2.addReservation(reservation3);
        assertNull(user2.getReservation(0));
    }

    @Test
    void testCheckPasswordReturnsTrueForValidPassword() {
        assertTrue(user.checkPassword("password123"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PASSWORD123", "Password123", "WrongPass"})
    void testCheckPasswordReturnsFalseForInvalidPassword(String inputPassword) {
        assertFalse(user.checkPassword(inputPassword));
    }
}
