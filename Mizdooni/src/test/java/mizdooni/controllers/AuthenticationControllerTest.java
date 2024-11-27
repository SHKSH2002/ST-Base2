package mizdooni.controllers;

import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.*;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import mizdooni.service.ServiceUtils;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import mizdooni.model.Address;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ServiceUtils;
import mizdooni.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


public class AuthenticationControllerTest {

    private User user1;
    private Address address1;
    private String username2;
    private String email2;

    private Map<String, String> address2;
    private String username;
    private String password;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks((this));
        address1 = new Address("Iran", "tehran", "AmirAbad");
        user1 = new User("username1", "password", "email1", address1, User.Role.client);
        username2 = "username2";
        address2 = new HashMap<>();
        address2.put("country", "Iran");
        address2.put("city", "Tehran");
        address2.put("street", "Amirabad");
        email2 = "email2@mail.com";
        username = "parsa";
        password = "123";
    }

    @Test
    void testUserReturnsSuccessfulResponse() throws Exception{
        when(userService.getCurrentUser()).thenReturn(user1);

        Response response = authenticationController.user();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertEquals("current user", response.getMessage());
        assertEquals(user1, response.getData());
    }

    @Test
    void testUserReturnsUnauthorized() throws Exception{
        when(userService.getCurrentUser()).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.user());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testLoginReturnsSuccessfulResponse() throws Exception{
        Map<String, String> params = new HashMap<>();

        params.put("username", username);
        params.put("password", password);

        when(userService.login(username, password)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user1);

        Response response = authenticationController.login(params);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("login successful", response.getMessage());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertNotNull(response.getData());
        assertEquals(user1, response.getData());
    }

    @Test
    void testLoginInvalidUsernameOrPassword() throws Exception{
        Map<String, String> params = new HashMap<>();

        params.put("username", username);
        params.put("password", password);

        when(userService.login(username, password)).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.login(params));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testLoginNullUsername() throws Exception{
        Map<String, String> params = new HashMap<>();

        params.put("username", username);
        params.put("password", "");

        when(userService.login(username, password)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.login(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testLoginNullPassword() throws Exception{
        Map<String, String> params = new HashMap<>();

        params.put("username", "");
        params.put("password", password);

        when(userService.login(username, password)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.login(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testSignupSuccessfulResponse() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", email2);
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        Response response = authenticationController.signup(params);

        when(userService.getCurrentUser()).thenReturn(user1);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("signup successful", response.getMessage());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        verify(userService).login(username2, password);
//        verify(userService).signup(username2, password, email2, new Address(address2.get("country"), address2.get("city"), null), User.Role.client);
    }

    @Test
    void testSignupParamMiss1() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", email2);
        params.put("address", address2);

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testSignupParamMiss2() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", email2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testSignupParamMiss3() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testSignupParamMiss4() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("email", email2);
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testSignupParamMiss5() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("password", password);
        params.put("email", email2);
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }
    @Test
    void testSignupParamBadTypeRole() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", email2);
        params.put("address", address2);
        params.put("role", User.Role.client);

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testSignupParamBadTypeAddress() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", email2);
        params.put("address", address1);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testSignupParamBadTypeEmail() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", user1);
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testSignupParamBadTypePassword() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", user1);
        params.put("email", email2);
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testSignupParamBadTypeUsername() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", user1);
        params.put("password", password);
        params.put("email", email2);
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testSignupParamMissingCity() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", email2);
        address2.remove("city");
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testSignupParamMissingCountry() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("username", username2);
        params.put("password", password);
        params.put("email", email2);
        address2.remove("country");
        params.put("address", address2);
        params.put("role", User.Role.client.toString());

        when(userService.getCurrentUser()).thenReturn(user1);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testLogoutReturnsSuccessfullResponse() throws Exception{
        when(userService.logout()).thenReturn(true);

        Response response = authenticationController.logout();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("logout successful", response.getMessage());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
    }

    @Test
    void testLogoutUnauthorized() throws Exception{
        when(userService.logout()).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.logout());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    void testValidateUsernameGetSuccessfulResponse() throws Exception{
        when(userService.usernameExists(username)).thenReturn(false);

        Response response = authenticationController.validateUsername(username);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("username is available", response.getMessage());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"parsa nassery", ""})
    void testValidateUsernameBadUsername(String usernameInput) throws Exception{

        when(userService.usernameExists(usernameInput)).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateUsername(usernameInput));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid username format", exception.getMessage());
    }

    @Test
    void testValidateUsernameExistUsername() throws Exception{
        when(userService.usernameExists(username)).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateUsername(username));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("username already exists", exception.getMessage());
    }

    @Test
    void testValidateEmailGetSuccessfulResponse() throws Exception{
        when(userService.emailExists(email2)).thenReturn(false);

        Response response = authenticationController.validateEmail(email2);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("email not registered", response.getMessage());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mail", "mail.com", "mail@mail", "mail.mail.com", "mail@mail@mail", "mail@mail@mail.com"})
    void testValidateEmailBadEmail(String mail) throws Exception{

        when(userService.emailExists(mail)).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateEmail(mail));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid email format", exception.getMessage());
    }

    @Test
    void testValidateEmailExistEmail() throws Exception{
        when(userService.emailExists(email2)).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateEmail(email2));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("email already registered", exception.getMessage());
    }
}
