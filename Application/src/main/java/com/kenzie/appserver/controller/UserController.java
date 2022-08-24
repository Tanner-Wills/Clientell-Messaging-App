package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.controller.model.UserResponse;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.User;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/Users")
public class UserController {

    private UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") String userId) throws Exception {
        User user = userService.findUserByUserId(userId);
        // if there are no users, return a 204
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Otherwise, convert it into a UserResponse and return it
        UserResponse userResponse = createUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping
    public ResponseEntity<UserResponse> addNewUser(@RequestBody UserCreateRequest userCreateRequest) throws Exception {
        List<String> threads = new ArrayList<>();
        User user = new User(randomUUID().toString(),
                userCreateRequest.getName(),
                threads);
        userService.addNewUser(user);

        UserResponse userResponse = createUserResponse(user);

        return ResponseEntity.created(URI.create("/Users/" + userResponse.getUserId())).body(userResponse);
    }

//    @PutMapping
//    public ResponseEntity<UserResponse> updateUser(@RequestBody User userUpdateRequest) {
//        User user = new User(userUpdateRequest.getUserId(),
//                userUpdateRequest.getName(),
//                userUpdateRequest.getThreads());
//        userService.updateUser(user);
//
//        UserResponse userResponse = createUserResponse(user);
//
//        return ResponseEntity.ok(userResponse);
//    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();

        // If there are no users, then return a 204
        if (users == null ||  users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Otherwise, convert the List of Concert objects into a List of ConcertResponses and return it
        List<UserResponse> response = new ArrayList<>();
        for (User user : users) {
            response.add(this.createUserResponse(user));
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUserById(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Helper method to create a UserResponse from a User
    private UserResponse createUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setName(user.getName());
        userResponse.setThreads(user.getThreads());
        return userResponse;
    }
}
