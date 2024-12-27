package mizdooni.model;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.zh_cn.而且;
import io.cucumber.spring.CucumberContextConfiguration;
import mizdooni.MizdooniApplication;
import mizdooni.response.Response;
import mizdooni.service.TableService;
import mizdooni.service.ReservationService;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import mizdooni.exceptions.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CucumberContextConfiguration
@SpringBootTest(classes = MizdooniApplication.class)
public class UserScenarioTest {
    private User user;
    private Restaurant restaurant;
    private User managerUser;
    private Address address = new Address("country", "city", "street");
    private Reservation reservation1;
    private Reservation reservation2;
    private int tableCount = 0;
    private Exception exception = null;
    private ReservationService reservationService = new ReservationService();

    @Given("A sample user")
    public void aSampleUser() {
        user = new User("username1", "password", "email@mail.com", address, User.Role.client);
    }

    @Given("A manager user")
    public void aManagerUser() {
        managerUser = new User("username2", "password", "email@mail.com", address, User.Role.manager);
    }

    @Given("A sample restaurant")
    public void aSampleRestaurant() {
        restaurant = new Restaurant("name", managerUser, "type", LocalTime.of(9, 0, 0),
                LocalTime.of(22, 0, 0), "description", address, "imageLink");
    }

    @Given("A sample table")
    public void aSampleTable() {
        Table tableTemp = new Table(++ tableCount, restaurant.getId(), 4);
        restaurant.addTable(tableTemp);
    }

    @When("Add Reservation with time {int} year {int} month {int} day {int} hour {int} minute")
    public void aSampleReservation(int year, int month, int day, int hour, int minute) {
        try{
            Table tableTemp = new Table(++ tableCount, restaurant.getId(), 4);

            reservation1 = new Reservation(
                    user, restaurant, tableTemp,
                    LocalDateTime.of(year, month, day, hour, minute)
            );
            user.addReservation(reservation1);
        }
        catch (Exception e){
            exception = e;
        }
    }

    @When("User Rating with overall {int} food {int} ambiance {int} service {int}")
    public void aUserReview(int overall, int food, int ambiance, int service) {
        try{
            Rating rating = new Rating();
            rating.overall = overall;
            rating.food = food;
            rating.ambiance = ambiance;
            rating.service = service;
            Review review = new Review(user, rating, "", LocalDateTime.now());
            restaurant.addReview(review);
        }
        catch (Exception e){
            exception = e;
        }
    }

    @When("Add Rating with overall {int} food {int} ambiance {int} service {int}")
    public void aSampleReview(int overall, int food, int ambiance, int service) {
        try{
            User tempUser = new User("user","123","abc@abc.com", address, User.Role.client);
            Rating rating = new Rating();
            rating.overall = overall;
            rating.food = food;
            rating.ambiance = ambiance;
            rating.service = service;
            Review review = new Review(tempUser, rating, "", LocalDateTime.now());
            restaurant.addReview(review);
        }
        catch (Exception e){
            exception = e;
        }
    }

    @Then("The count of reservation should be {int}")
    public void theUserCreditShouldBe(int count) {
        assertEquals(count, user.getReservations().size());
    }

    @Then("The count of review should be {int}")
    public void countOfReview(int count){
        assertEquals(count, restaurant.getReviews().size());
    }

    @Then("The average of review should be overall {double} food {double} ambiance {double} service {double}")
    public void averageOfReview(double overall, double food, double ambiance, double service){
        assertEquals(overall, restaurant.getAverageRating().overall);
        assertEquals(food, restaurant.getAverageRating().food);
        assertEquals(ambiance, restaurant.getAverageRating().ambiance);
        assertEquals(service, restaurant.getAverageRating().service);
    }
}
