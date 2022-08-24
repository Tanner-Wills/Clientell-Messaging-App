package com.kenzie.appserver.controller;

import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.UserCreateRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.User;
import io.restassured.internal.common.assertion.Assertion;
import net.andreinc.mockneat.MockNeat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    UserService userService;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();


    /** ------------------------------------------------------------------------
     *  userController @GetMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void getById_Exists() throws Exception {
        // GIVEN
        String id = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        List<String> threads = new ArrayList<>();
        threads.add("test1");

        User user = new User(id, name, threads);

        // WHEN
        User persistedUser = userService.addNewUser(user);

        // THEN
        mvc.perform(get("/Users/{userId}", persistedUser.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("userId")
                        .value(is(id)))
                .andExpect(jsonPath("name")
                        .value(is(name)))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_Fails() throws Exception {
        // GIVEN
        String id = UUID.randomUUID().toString();

        // WHEN
        // THEN
        Assertions.assertThrows(Exception.class, () ->
        mvc.perform(get("/Users/{userId}", id)
                        .accept(MediaType.APPLICATION_JSON)));
    }


    /** ------------------------------------------------------------------------
     *  userController @PostMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void createUser_CreateSuccessful() throws Exception {
        // GIVEN
        String id = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        Set<String> threads = new HashSet<>();
        threads.add("test1");

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(name);

        mapper.registerModule(new JavaTimeModule());

        // WHEN / THEN
        mvc.perform(post("/Users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateRequest)))
                .andExpect(jsonPath("userId")
                        .exists())
                .andExpect(jsonPath("name")
                        .value(is(name)))
                .andExpect(jsonPath("threads")
                        .exists())
                .andExpect(status().isCreated());
    }

    @Test
    public void createUserWithSameName_Fails() throws Exception {
        // GIVEN
        String id = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        Set<String> threads = new HashSet<>();
        threads.add("test1");

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(name);

        mapper.registerModule(new JavaTimeModule());

        mvc.perform(post("/Users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateRequest)));

        // WHEN
        // THEN
        Assertions.assertThrows(Exception.class, () ->
                mvc.perform(post("/Users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateRequest))));
    }

    /** ------------------------------------------------------------------------
     *  userController @PutMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void updateUser_Successful() throws Exception {
        // GIVEN
        String id = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        Set<String> threads = new HashSet<>();
        threads.add("test1");

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(name);

        mapper.registerModule(new JavaTimeModule());

        mvc.perform(post("/Users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateRequest)));

        // WHEN


        // THEN

    }


    /** ------------------------------------------------------------------------
     *  userController @DeleteMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void deleteUser_Successful() throws Exception {
        // GIVEN
        String id = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        List<String> threads = new ArrayList<>();
        threads.add("test1");

        User user = new User(id, name, threads);

        // WHEN
        User persistedUser = userService.addNewUser(user);

        // THEN
        mvc.perform(delete("/Users/{userId}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    public void deleteUser_Fails() throws Exception {
        // GIVEN
        String id = UUID.randomUUID().toString();

        // WHEN
        // THEN
        Assertions.assertThrows(Exception.class, () ->
        mvc.perform(delete("/Users/{userId}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204)));
    }
}
