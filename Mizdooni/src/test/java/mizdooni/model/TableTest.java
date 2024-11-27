package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {
    private Table table;
    private Reservation reservation1;
    private Reservation reservation2;
    private Address address;
    private Address address2;
    private User user;
    private User managerUser;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        table = new Table(1, 1, 4);
        address = new Address("country", "city", "street");
        address2 = new Address("country2", "city2", "street2");
        user = new User("username", "password", "email", address, User.Role.manager);
        managerUser = new User("username2", "password2", "email2", address, User.Role.client);
        restaurant = new Restaurant("name", managerUser, "type", LocalTime.of(9, 0, 0),
                LocalTime.of(22, 0, 0), "description", address2, "imageLink");
        reservation1 = new Reservation(
                user, restaurant, table,
                LocalDateTime.of(2024, 10, 21, 18, 0)
        );
            reservation2 = new Reservation(
                    user, restaurant, table,
                    LocalDateTime.of(2024, 10, 21, 19, 0)
            );
        reservation2.cancel();
    }

    @Test
    void testAddReservation() {
        table.addReservation(reservation1);
        assertEquals(1, table.getReservations().size());
    }

    @Test
    void testIsReservedReturnsTrueInReservedTime() {
        table.addReservation(reservation1);
        assertTrue(table.isReserved(reservation1.getDateTime()));
    }

    @Test
    void testIsReservedReturnsFalseInNotReservedTime() {
        assertFalse(table.isReserved(reservation1.getDateTime()));
    }

    @Test
    void testIsReservedReturnsFalseInCanceledReservation() {
        table.addReservation(reservation2);
        assertFalse(table.isReserved(reservation2.getDateTime()));
    }
}