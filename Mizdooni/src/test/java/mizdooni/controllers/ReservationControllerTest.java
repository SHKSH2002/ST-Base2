package mizdooni.controllers;

import mizdooni.exceptions.*;
import mizdooni.model.*;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {
    private Table table;
    private Reservation reservation1;
    private Reservation reservation2;
    private Address address;
    private Address address2;
    private User user;
    private User managerUser;
    private Restaurant restaurant;

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void testGetReservationsReturnsSuccessfulResponse() throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        LocalDate date = LocalDate.now();

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.getReservations(restaurant.getId(), table.getTableNumber(), date)).thenReturn(List.of(reservation1));

        Response response = reservationController.getReservations(restaurant.getId(), table.getTableNumber(), date.toString());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("restaurant table reservations", response.getMessage());
        assertEquals(List.of(reservation1), response.getData());
        assertNull(response.getError());
        verify(reservationService).getReservations(restaurant.getId(), table.getTableNumber(), date);
    }

    @Test
    void testGetReservationsReturnsSuccessfulResponseWithoutDate() throws UserNotManager, TableNotFound, InvalidManagerRestaurant, RestaurantNotFound {
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.getReservations(restaurant.getId(), table.getTableNumber(), null)).thenReturn(List.of(reservation1));

        Response response = reservationController.getReservations(restaurant.getId(), table.getTableNumber(), null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("restaurant table reservations", response.getMessage());
        assertEquals(List.of(reservation1), response.getData());
        assertNull(response.getError());
        verify(reservationService).getReservations(restaurant.getId(), table.getTableNumber(), null);
    }

    @Test
    void testGetReservationsRaiseWithInvalidDateFormat() {
        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getReservations(restaurant.getId(), table.getTableNumber(), "invalid-date");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testGetReservationsRaiseForNotExistingRestaurant() {
        LocalDate date = LocalDate.now();

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getReservations(restaurant.getId(), table.getTableNumber(), date.toString());
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }

    @Test
    void testGetCustomerReservationsReturnsSuccessfulResponse() throws UserNotFound, UserNoAccess {
        when(reservationService.getCustomerReservations(user.getId())).thenReturn(List.of(reservation1));

        Response response = reservationController.getCustomerReservations(user.getId());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("user reservations", response.getMessage());
        assertEquals(List.of(reservation1), response.getData());
        assertNull(response.getError());
        verify(reservationService).getCustomerReservations(user.getId());
    }

    @Test
    void testGetCustomerReservationsRaiseForNotExistingUser() throws UserNotFound, UserNoAccess {
        when(reservationService.getCustomerReservations(user.getId())).thenThrow(new UserNotFound());

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getCustomerReservations(user.getId());
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void testGetAvailableTimesReturnsSuccessfulResponse() throws DateTimeInThePast, RestaurantNotFound, BadPeopleNumber {
        int people = 4;
        LocalDate date = LocalDate.now();
        List<LocalTime> availableTimes = List.of(LocalTime.of(12, 0), LocalTime.of(13, 0));

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.getAvailableTimes(restaurant.getId(), people, date)).thenReturn(availableTimes);

        Response response = reservationController.getAvailableTimes(restaurant.getId(), people, date.toString());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("available times", response.getMessage());
        assertEquals(availableTimes, response.getData());
        assertNull(response.getError());
        verify(reservationService).getAvailableTimes(restaurant.getId(), people, date);
    }

    @Test
    void testGetAvailableTimesRaiseWithInvalidDateFormat() {
        int people = 4;

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getAvailableTimes(restaurant.getId(), people, "invalid-date");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testGetAvailableTimesRaiseForNotExistingRestaurant() {
        int people = 4;
        LocalDate date = LocalDate.now();

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getAvailableTimes(restaurant.getId(), people, date.toString());
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("restaurant not found", exception.getMessage());
    }

    @Test
    void testAddReservationReturnsSuccessfulResponse() throws UserNotFound, DateTimeInThePast, TableNotFound, ReservationNotInOpenTimes, ManagerReservationNotAllowed, RestaurantNotFound, InvalidWorkingTime {
        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", "2024-11-01 23:00");


        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reservationService.reserveTable(eq(restaurant.getId()), eq(4), any(LocalDateTime.class))).thenReturn(reservation1);

        Response response = reservationController.addReservation(restaurant.getId(), params);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("reservation done", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void testAddReservationRaiseWithMissingParams() {
        Map<String, String> params = new HashMap<>();

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurant.getId(), params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testAddReservationRaiseWithInvalidDateTimeFormat() {
        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", "invalid-datetime");

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurant.getId(), params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testAddReservationRaiseWithInvalidPeopleFormat() {
        Map<String, String> params = new HashMap<>();
        params.put("people", "4s");
        params.put("datetime", "2024-11-01 23:00");

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurant.getId(), params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testCancelReservationReturnsSuccessfulResponse() throws ReservationCannotBeCancelled, UserNotFound, ReservationNotFound {
        doNothing().when(reservationService).cancelReservation(reservation1.getReservationNumber());

        Response response = reservationController.cancelReservation(reservation1.getReservationNumber());

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("reservation cancelled", response.getMessage());
        verify(reservationService).cancelReservation(reservation1.getReservationNumber());
    }

    @Test
    void testCancelReservation_ReservationNotFound() throws ReservationCannotBeCancelled, UserNotFound, ReservationNotFound {
        int reservationNumber = 1;

        doThrow(new ReservationNotFound()).when(reservationService).cancelReservation(reservationNumber);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.cancelReservation(reservationNumber);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Reservation not found.", exception.getMessage());
    }
}
