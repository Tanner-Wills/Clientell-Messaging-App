package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.appserver.service.model.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    /** ------------------------------------------------------------------------
     *  userService.addNewUser
     *  ------------------------------------------------------------------------ **/

    @Test
    void addNewUser_Success() throws Exception {
        // GIVEN
        String userId = randomUUID().toString();
        List<String> threads = new ArrayList<>();
        threads.add("testThreadId");

        User user = new User(userId, "testUser", threads);

        ArrayList<UserRecord> records = new ArrayList<>();

        ArgumentCaptor<UserRecord> userRecordArgumentCaptor = ArgumentCaptor.forClass(UserRecord.class);

        when(userRepository.findAll()).thenReturn(records);
        // WHEN
        User returnedUser = userService.addNewUser(user);

        // THEN
        Assertions.assertNotNull(returnedUser);

        verify(userRepository).save(userRecordArgumentCaptor.capture());

        UserRecord record = userRecordArgumentCaptor.getValue();


        Assertions.assertNotNull(record, "The user record is returned");
        Assertions.assertEquals(record.getUserId(), user.getUserId(), "The userId matches");
        Assertions.assertEquals(record.getName(), user.getName(), "The user name matches");
        Assertions.assertEquals(record.getThreads(), user.getThreads(), "The user thread matches");
    }


    @Test
    void addNewUser_WithSameName_ThrowsException() throws Exception {
        // GIVEN
        List<String> threads = new ArrayList<>();
        threads.add("testThreadId");
        User user = new User(randomUUID().toString(), "testUser", threads);

        UserRecord mockRecord = new UserRecord();
        mockRecord.setUserId(user.getUserId());
        mockRecord.setName(user.getName());
        mockRecord.setThreads(user.getThreads());

        ArrayList<UserRecord> records = new ArrayList<>();
        records.add(mockRecord);

        User userSame = new User(randomUUID().toString(), user.getName(), threads);

        ArgumentCaptor<UserRecord> userRecordArgumentCaptor = ArgumentCaptor.forClass(UserRecord.class);

        when(userRepository.findAll()).thenReturn(records);

        // WHEN

        // THEN
        Assertions.assertThrows(Exception.class, ()-> userService.addNewUser(userSame));
    }


    /** ------------------------------------------------------------------------
     *  userService.findAllUsers
     *  ------------------------------------------------------------------------ **/

    @Test
    void findAllUsers_Success() {
        // GIVEN
        List<String> threads = new ArrayList<>();
        threads.add("TestThreadId");
        UserRecord record1 = new UserRecord();
        record1.setUserId(randomUUID().toString());
        record1.setName("user1");
        record1.setThreads(threads);

        UserRecord record2 = new UserRecord();
        record2.setUserId(randomUUID().toString());
        record2.setName("user2");
        record1.setThreads(threads);

        List<UserRecord> recordList = new ArrayList<>();
        recordList.add(record1);
        recordList.add(record2);
        when(userRepository.findAll()).thenReturn(recordList);

        // WHEN
        List<User> users = userService.findAllUsers();

        // THEN
        Assertions.assertNotNull(users, "The users list is returned");
        Assertions.assertEquals(2, users.size(), "There are two users");

        for (User user : users) {
            if (user.getUserId() == record1.getUserId()) {
                Assertions.assertEquals(record1.getUserId(), user.getUserId(), "The user id matches");
                Assertions.assertEquals(record1.getName(), user.getName(), "The user name matches");
                Assertions.assertEquals(record1.getThreads(), user.getThreads(), "The user threads match");

            } else if (user.getUserId() == record2.getUserId()) {
                Assertions.assertEquals(record2.getUserId(), user.getUserId(), "The user id matches");
                Assertions.assertEquals(record2.getName(), user.getName(), "The user name matches");
                Assertions.assertEquals(record2.getThreads(), user.getThreads(), "The user threads match");

            } else {
                assertTrue(false, "User returned that was not in the records!");
            }
        }
    }

    /** ------------------------------------------------------------------------
     *  userService.findUserById
     *  ------------------------------------------------------------------------ **/

    @Test
    void findByUserId_Success() throws Exception {
        // GIVEN
        String userId = randomUUID().toString();
        List<String> threads = new ArrayList<>();
        threads.add("TestThreadId");

        UserRecord record = new UserRecord();
        record.setUserId(userId);
        record.setName("testUser");
        record.setThreads(threads);
        when(userRepository.findById(userId)).thenReturn(Optional.of(record));

        // WHEN
        User user = userService.findUserByUserId(userId);

        // THEN
        Assertions.assertNotNull(user, "The user is returned");
        Assertions.assertEquals(record.getUserId(), user.getUserId(), "The user id matches");
        Assertions.assertEquals(record.getName(), user.getName(), "The user name matches");
        Assertions.assertEquals(record.getThreads(), user.getThreads(), "The threads match");
    }

    /** ------------------------------------------------------------------------
     *  userService.updateUser
     *  ------------------------------------------------------------------------ **/

    @Test
    void userService_successfullyUpdatesUser() {
        // GIVEN
        String userId = randomUUID().toString();
        List<String> threads = new ArrayList<>();
        threads.add("TestThreadId");

        User user = new User(userId, "testUser", threads);

        when(userRepository.existsById(userId)).thenReturn(true);

        // WHEN
        userService.updateUser(user);

        // THEN
        verify(userRepository, times(1)).save(any());
    }

    /** ------------------------------------------------------------------------
     *  userService.updateUser
     *  ------------------------------------------------------------------------ **/

    @Test
    void userService_deletesUser() {
        // GIVEN
        String userId = randomUUID().toString();
        List<String> threads = new ArrayList<>();
        threads.add("TestThreadId");

        User user = new User(userId, "testUser", threads);

        // WHEN
        userService.deleteUser(userId);

        // THEN
        verify(userRepository, times(1)).deleteById(userId);
    }

    /** ------------------------------------------------------------------------
     *  userService.findUserByName
     *  ------------------------------------------------------------------------ **/

    @Test
    void findUserByName_returnsUser() {
        //GIVEN
        List<String> threads = new ArrayList<>();
        threads.add("TestThreadId");

        UserRecord record1 = new UserRecord();
        record1.setUserId(randomUUID().toString());
        record1.setName("user1");
        record1.setThreads(threads);

        UserRecord record2 = new UserRecord();
        record2.setUserId(randomUUID().toString());
        record2.setName("user2");
        record2.setThreads(threads);

        List<UserRecord> recordList = new ArrayList<>();
        recordList.add(record1);
        recordList.add(record2);
        when(userRepository.findAll()).thenReturn(recordList);

        //WHEN
        User result = userService.findUserByName("user1");

        //THEN
        verify(userRepository, times(1)).findAll();
        Assertions.assertEquals("user1", result.getName());
    }

    @Test
    void findUserByName_userNotFound_throwsException() {
        //GIVEN - user not in the database
        String name = "testUser";

        //WHEN
        //THEN
        Assertions.assertThrows(NullPointerException.class, () -> userService.findUserByName(name));
    }
}

