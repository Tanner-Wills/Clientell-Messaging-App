package com.kenzie.appserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.ThreadCreateRequest;
import com.kenzie.appserver.service.MessageService;
import com.kenzie.appserver.service.ThreadService;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.Message;
import com.kenzie.appserver.service.model.Threads;
import com.kenzie.appserver.service.model.User;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class ThreadControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    UserService userService;

    @Autowired
    ThreadService threadService;

    @Autowired
    MessageService messageService;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();

    /** ------------------------------------------------------------------------
     *  threadController.getThreadById @GetMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void getByThreadId_Success() throws Exception {
        // GIVEN
        // create two user objects to post to user table
        List<String> userThreads = new ArrayList<>();
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();

        User user1 = new User(userId1, mockNeat.strings().valStr(), userThreads);
        User user2 = new User(userId2, mockNeat.strings().valStr(), userThreads);
        User persistedUser1 = userService.addNewUser(user1);
        User persistedUser2 = userService.addNewUser(user2);

        // confirm users are in the table first
        mvc.perform(get("/Users/{userId}",persistedUser1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/Users/{userId}",persistedUser2.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // create thread to post
        String threadId = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        List<String> users = new ArrayList<>();
        users.add(userId1);
        users.add(userId2);
        List<String> messages = new ArrayList<>();


        // then post the thread
        Threads threads = new Threads(threadId, name, users, messages);
        Threads persistedThreads = threadService.addNewThread(threads);

        // post a message to the thread
        String messageId = UUID.randomUUID().toString();
        Message messageTest = new Message(
                messageId,
                LocalDateTime.now().toString(),
                persistedThreads.getThreadId(),
                userId1,
                "hello");
        threadService.addNewMessage(messageTest);

        // confirm message is in the table and content exists
        mvc.perform(get("/Messages/{messageId}",messageId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("message")
                        .value(is("hello")))
                .andExpect(status().isOk());

        // WHEN / THEN
        mvc.perform(get("/Threads/{threadId}", persistedThreads.getThreadId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getByThreadId_Fail() throws Exception {
        // GIVEN
        String threadId = UUID.randomUUID().toString();

        // WHEN / THEN
        Assertions.assertThrows(Exception.class, () ->
                mvc.perform(get("/Threads/{threadId}", threadId)
                        .accept(MediaType.APPLICATION_JSON)));
    }

    /** ------------------------------------------------------------------------
     *  threadController.createThread @PostMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void createThread_CreateSuccessful() throws Exception {
        // create two user objects to post to user table
        List<String> userThreads = new ArrayList<>();
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();

        User user1 = new User(userId1, mockNeat.strings().valStr(), userThreads);
        User user2 = new User(userId2, mockNeat.strings().valStr(), userThreads);
        User persistedUser1 = userService.addNewUser(user1);
        User persistedUser2 = userService.addNewUser(user2);

        // confirm users are in the table first
        mvc.perform(get("/Users/{userId}",persistedUser1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/Users/{userId}",persistedUser2.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // make ThreadCreateRequest to post
        String name = mockNeat.strings().valStr();
        List<String> users = new ArrayList<>();
        users.add(userId1);
        users.add(userId2);

        ThreadCreateRequest threadCreateRequest = new ThreadCreateRequest();
        threadCreateRequest.setThreadTitle(name);
        threadCreateRequest.setUsers(users);

        mapper.registerModule(new JavaTimeModule());

        mvc.perform(post("/Threads")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(threadCreateRequest)))
                .andExpect(jsonPath("threadId")
                        .exists())
                .andExpect(jsonPath("threadTitle")
                        .value(is(name)))
                .andExpect(jsonPath("users")
                        .exists())
                .andExpect(jsonPath("messages")
                        .exists())
                .andExpect(status().isCreated());
    }

    @Test
    public void createThread_withUser_CreateFail() throws Exception {
        // create one user object to post to user table
        List<String> userThreads = new ArrayList<>();
        String userId1 = UUID.randomUUID().toString();
        User user1 = new User(userId1, mockNeat.strings().valStr(), userThreads);
        User persistedUser1 = userService.addNewUser(user1);


        // confirm users are in the table first
        mvc.perform(get("/Users/{userId}",persistedUser1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        // make ThreadCreateRequest to post
        String userId2 = UUID.randomUUID().toString();
        String name = mockNeat.strings().valStr();
        List<String> users = new ArrayList<>();
        users.add(userId1);
        users.add(userId2);

        ThreadCreateRequest threadCreateRequest = new ThreadCreateRequest();
        threadCreateRequest.setThreadTitle(name);
        threadCreateRequest.setUsers(users);

        mapper.registerModule(new JavaTimeModule());

        Assertions.assertThrows(Exception.class, () ->
                mvc.perform(post("/Threads")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(threadCreateRequest))));
    }

    /** ------------------------------------------------------------------------
     *  threadController.updateThread @PutMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void updateThread_Successful() throws Exception {
        // GIVEN
        // Successfully post a thread
        // create two user objects to post to user table
        List<String> userThreads = new ArrayList<>();
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();

        User user1 = new User(userId1, mockNeat.strings().valStr(), userThreads);
        User user2 = new User(userId2, mockNeat.strings().valStr(), userThreads);
        User persistedUser1 = userService.addNewUser(user1);
        User persistedUser2 = userService.addNewUser(user2);

        // confirm users are in the table first
        mvc.perform(get("/Users/{userId}",persistedUser1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/Users/{userId}",persistedUser2.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // make ThreadCreateRequest to post
        String name = mockNeat.strings().valStr();
        List<String> users = new ArrayList<>();
        users.add(userId1);
        users.add(userId2);

        ThreadCreateRequest threadCreateRequest = new ThreadCreateRequest();
        threadCreateRequest.setThreadTitle(name);
        threadCreateRequest.setUsers(users);

        mapper.registerModule(new JavaTimeModule());

        mvc.perform(post("/Threads")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(threadCreateRequest)))
                .andExpect(jsonPath("threadId")
                        .exists())
                .andExpect(jsonPath("threadTitle")
                        .value(is(name)))
                .andExpect(jsonPath("users")
                        .exists())
                .andExpect(jsonPath("messages")
                        .exists())
                .andExpect(status().isCreated());

        // WHEN
        // Updating a thread


        // THEN

    }


    /** ------------------------------------------------------------------------
     *  threadController.deleteThread @DeleteMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    public void deleteThread_Successful() throws Exception {
        // GIVEN
        // Post a thread to the repository
        // create two user objects to post to user table
        List<String> userThreads = new ArrayList<>();
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();

        User user1 = new User(userId1, mockNeat.strings().valStr(), userThreads);
        User user2 = new User(userId2, mockNeat.strings().valStr(), userThreads);
        User persistedUser1 = userService.addNewUser(user1);
        User persistedUser2 = userService.addNewUser(user2);

        // confirm users are in the table first
        mvc.perform(get("/Users/{userId}", persistedUser1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/Users/{userId}", persistedUser2.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // make ThreadCreateRequest to post
        String name = mockNeat.strings().valStr();
        List<String> users = new ArrayList<>();
        users.add(userId1);
        users.add(userId2);

        ThreadCreateRequest threadCreateRequest = new ThreadCreateRequest();
        threadCreateRequest.setThreadTitle(name);
        threadCreateRequest.setUsers(users);

        mapper.registerModule(new JavaTimeModule());
        String threadId = UUID.randomUUID().toString();
        List<String> messages = new ArrayList<>();
        Threads threads = new Threads(threadId, "name", users, messages);
        Threads persistedThreads = threadService.addNewThread(threads);

        // WHEN
        // attempting to delete a thread
        // THEN
        mvc.perform(delete("/Threads/{threadId}", threadId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    public void deleteThread_Fail() throws Exception {
        // GIVEN
        String threadId = UUID.randomUUID().toString();

        // WHEN
        // attempting to delete a thread
        // THEN
        Assertions.assertThrows(Exception.class, () ->
                mvc.perform(delete("/Threads/{threadId}", threadId)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(204)));
    }
}
