package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.UserRecord;

import com.kenzie.appserver.service.model.User;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * CRUD methods for Users
     */

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();

        Iterable<UserRecord> userRecordIterator = userRepository.findAll();
        for (UserRecord record: userRecordIterator) {
            users.add(new User(record.getUserId(),
                    record.getName(),
                    record.getThreads()));
        }
        return users;
    }

    public User findUserByUserId(String userId) throws Exception {
        // find the user
        User userFromBackendService = userRepository.findById(userId)
                .map(user -> new User(user.getUserId(),
                        user.getName(),
                        user.getThreads()))
                .orElse(null);

        if (userFromBackendService == null) {
            throw new Exception("User is not is repository!");
        }

        // return user
        return userFromBackendService;
    }

    public User findUserByName(String name) {
        // populate list with all UserRecords in the userRepository
        Iterable<UserRecord> userRecords = userRepository.findAll();

        // Loop through and see if there is a hit.
        UserRecord userSearch = null;
        for (UserRecord record : userRecords) {
            if (record.getName().equals(name)) {
                userSearch = record;
            }
        }

        // if user is not found, throw exception
        if (userSearch == null) {
            throw new NullPointerException("User was not found in the database!");
        }

        // return user
        return new User(userSearch.getUserId(), userSearch.getName(), userSearch.getThreads());
    }


    public User addNewUser(User user) throws Exception {
        checkForUserNameDuplicates(user);
        userRepository.save(createUserRecord(user));
        return user;
    }

    public void updateUser(User user) {
        userRepository.save(createUserRecord(user));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Helper Methods
     */

    private UserRecord createUserRecord(User user) {
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(user.getUserId());
        userRecord.setName(user.getName());
        userRecord.setThreads(user.getThreads());
        return userRecord;
    }
    private void checkForUserNameDuplicates(User user) throws Exception {
        Iterable<UserRecord> userNames = userRepository.findAll();
        for (UserRecord record : userNames) {
            if (record.getName().toLowerCase(Locale.ROOT).equals(user.getName().toLowerCase(Locale.ROOT))) {
                throw new Exception("UserName already exists!");
            }
        }
    }
}
