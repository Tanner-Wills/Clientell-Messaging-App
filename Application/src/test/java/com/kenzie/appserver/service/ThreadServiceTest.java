package com.kenzie.appserver.service;

import com.kenzie.appserver.Exceptions.DuplicateThreadException;
import com.kenzie.appserver.Exceptions.InvalidUserCountException;
import com.kenzie.appserver.repositories.MessageRepository;
import com.kenzie.appserver.repositories.ThreadRepository;
import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.MessageRecord;
import com.kenzie.appserver.repositories.model.ThreadRecord;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.appserver.service.model.Message;
import com.kenzie.appserver.service.model.Threads;

import com.kenzie.appserver.service.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZonedDateTime;
import java.util.*;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ThreadServiceTest {
    private UserRepository userRepository;
    private ThreadRepository threadRepository;
    private MessageRepository messageRepository;
    private UserService userService;
    private MessageService messageService;
    private ThreadService threadService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        threadRepository = mock(ThreadRepository.class);
        messageRepository = mock(MessageRepository.class);
        userService = new UserService(userRepository);
        messageService = new MessageService(messageRepository);
        threadService = new ThreadService(threadRepository, messageService, userService);
    }

    // TODO: check for code coverage

    /** ------------------------------------------------------------------------
     *  ThreadService.addNewMessage
     *  ------------------------------------------------------------------------ **/
    @Test
    void addNewMessage_savesMessage_returnsMessage() throws Exception {
        //GIVEN
        String messageId = randomUUID().toString();
        String timestamp = ZonedDateTime.now().toString();
        String threadId = randomUUID().toString();
        String sender = "sender";
        String recipient = "recipient";
        String messageContent = "test message";

        List<String> users = new ArrayList<>();
        users.add(sender);
        users.add(recipient);

        List<String> messages = new ArrayList<>();
        messages.add(messageId);

        Message message = new Message(messageId, timestamp, threadId, sender, messageContent);

        ThreadRecord threadRecord = new ThreadRecord();
        threadRecord.setThreadId(threadId);
        threadRecord.setThreadTitle("testName");
        threadRecord.setUsers(users);
        threadRecord.setMessages(messages);

        ArgumentCaptor<MessageRecord> messageRecordArgumentCaptor = ArgumentCaptor.forClass(MessageRecord.class);

        when(threadRepository.findById(any())).thenReturn(Optional.of(threadRecord));

        //WHEN
        Message result = threadService.addNewMessage(message);

        //THEN
        Assertions.assertNotNull(result);
        verify(messageRepository).save(messageRecordArgumentCaptor.capture());

        MessageRecord messageRecordResult = messageRecordArgumentCaptor.getValue();
        Assertions.assertNotNull(messageRecordResult);

        Assertions.assertEquals(message.getMessageId(), messageRecordResult.getMessageId());
        Assertions.assertEquals(message.getTimeStamp(), messageRecordResult.getTimeStamp());
        Assertions.assertEquals(message.getThreadId(), messageRecordResult.getThreadId());
        Assertions.assertEquals(message.getSender(), messageRecordResult.getSender());
        Assertions.assertEquals(message.getMessage(), messageRecordResult.getMessage());

        Assertions.assertEquals(message.getMessageId(), result.getMessageId());
        Assertions.assertEquals(message.getTimeStamp(), result.getTimeStamp());
        Assertions.assertEquals(message.getThreadId(), result.getThreadId());
        Assertions.assertEquals(message.getSender(), result.getSender());
        Assertions.assertEquals(message.getMessage(), result.getMessage());
    }

    @Test
    void addNewMessage_updateThreadWithMessageId() throws Exception {
        //GIVEN
        String messageId = randomUUID().toString();
        String newMessageId = randomUUID().toString();
        String timestamp = ZonedDateTime.now().toString();
        String threadId = randomUUID().toString();
        String threadTitle = "testThreadTitle";
        String sender = "sender";
        String recipient = "recipient";
        String messageContent = "test message";

        List<String> users = new ArrayList<>();
        users.add(sender);
        users.add(recipient);

        List<String> messages = new ArrayList<>();
        messages.add(messageId);

        Message newMessage = new Message(newMessageId, timestamp, threadId, sender, messageContent);

        Threads threadToUpdate = new Threads(threadId, threadTitle, users, messages);

        ThreadRecord threadRecord = new ThreadRecord();
        threadRecord.setThreadId(threadId);
        threadRecord.setThreadTitle(threadTitle);
        threadRecord.setUsers(users);
        threadRecord.setMessages(messages);
        when(threadRepository.findById(any())).thenReturn(Optional.of(threadRecord));

        //WHEN
        threadService.addNewMessage(newMessage);

        //THEN
        Assertions.assertTrue(threadToUpdate.getMessages().contains(newMessageId));
    }

    @Test
    void addNewMessage_nullMessage_throwsException() {
        //GIVEN - null message
        Message message = null;

        //WHEN - adding null message
        //THEN - exception is thrown
        Assertions.assertThrows(NullPointerException.class, () -> threadService.addNewMessage(message));
    }
    /** ------------------------------------------------------------------------
     *  ThreadService.addNewThread
     *  ------------------------------------------------------------------------ **/

    @Test
    void addNewThread_Success() throws Exception {
        // GIVEN
        List<String> userThreadsTest = new ArrayList<>();
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId("testId");
        userRecord.setName("Name");
        userRecord.setThreads(userThreadsTest);

        String threadId = randomUUID().toString();
        List<String> users = new ArrayList<>();
        users.add("test user1");
        users.add("test user2");
        List<String> messages = new ArrayList<>();
        messages.add("test message");
        Threads threads = new Threads(threadId, "testThread", users, messages);

        ThreadRecord threadRecord = new ThreadRecord();
        threadRecord.setThreadId("testThread");
        threadRecord.setThreadTitle("testName");
        threadRecord.setUsers(users);
        threadRecord.setMessages(messages);

        ArrayList<ThreadRecord> records = new ArrayList<>();

        ArgumentCaptor<ThreadRecord> threadRecordArgumentCaptor = ArgumentCaptor.forClass(ThreadRecord.class);

        when(userRepository.findById(any())).thenReturn(Optional.of(userRecord));
        when(threadRepository.findById(any())).thenReturn(Optional.of(threadRecord));
        when(threadRepository.findAll()).thenReturn(records);

        // WHEN
        Threads returnedThread = threadService.addNewThread(threads);

        // THEN
        Assertions.assertNotNull(returnedThread);

        verify(threadRepository).save(threadRecordArgumentCaptor.capture());

        ThreadRecord record = threadRecordArgumentCaptor.getValue();

        Assertions.assertNotNull(record, "The user record is returned");
        Assertions.assertEquals(record.getThreadId(), threads.getThreadId(), "The threadId matches");
        Assertions.assertEquals(record.getThreadTitle(), threads.getThreadTitle(), "The thread title matches");
        Assertions.assertEquals(record.getUsers(), threads.getUsers(), "The users match");
        Assertions.assertEquals(record.getMessages(), threads.getMessages(), "The messages match");

        threadService.deleteThread(threadId);
    }

    @Test
    void addNullThread_ThrowsException() {
        Assertions.assertThrows(Exception.class,() -> threadService.addNewThread(null));
    }

    @Test
    void addNewThread_UpdatesUserTable() throws Exception {
        //GIVEN - a new thread
        String threadId = randomUUID().toString();
        String threadTitle = "testThreadTitle";
        String userId1 = randomUUID().toString();
        String userId2 = randomUUID().toString();

        List<String> users = new ArrayList<>();
        users.add(userId1);
        users.add(userId2);

        List<String> messages = new ArrayList<>();
        messages.add("test message");

        Threads threads = new Threads(threadId, threadTitle, users, messages);

        List<String> threadIdList = new ArrayList<>();
        threadIdList.add(randomUUID().toString());

        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(randomUUID().toString());
        userRecord.setName("user1");
        userRecord.setThreads(threadIdList);

        ThreadRecord threadRecord1 = new ThreadRecord();
        threadRecord1.setThreadId(randomUUID().toString());
        threadRecord1.setThreadTitle("thread1");
        threadRecord1.setUsers(users);
        threadRecord1.setMessages(messages);

        ThreadRecord threadRecord2 = new ThreadRecord();
        threadRecord2.setThreadId(randomUUID().toString());
        threadRecord2.setThreadTitle("thread2");
        threadRecord2.setUsers(users);
        threadRecord2.setMessages(messages);

        List<ThreadRecord> recordList = new ArrayList<>();
        recordList.add(threadRecord1);
        recordList.add(threadRecord2);

        //WHEN - thread is added
        when(threadRepository.findAll()).thenReturn(recordList);
        when(userRepository.findById(any())).thenReturn(Optional.of(userRecord));
        User user1 = userService.findUserByUserId(userId1);
        User user2 = userService.findUserByUserId(userId2);

        threadService.addNewThread(threads);

        //THEN - verify that threadId is added to Users
        verify(userRepository, times(2)).save(userRecord);
        assertTrue(user1.getThreads().contains(threadId));
        assertTrue(user2.getThreads().contains(threadId));
    }

    @Test
    void addNewThread_ChecksUsersListSize_throwsException() throws Exception {
        //GIVEN - thread with one user
        String threadId = randomUUID().toString();
        String threadTitle = "testThreadTitle";
        String userId1 = randomUUID().toString();

        List<String> users = new ArrayList<>();
        users.add(userId1);

        List<String> messages = new ArrayList<>();
        messages.add("test message");

        Threads threads = new Threads(threadId, threadTitle, users, messages);

        List<String> threadIdList = new ArrayList<>();
        threadIdList.add(threads.getThreadId());

        UserRecord userRecord1 = new UserRecord();
        userRecord1.setUserId(userId1);
        userRecord1.setName("user1");
        userRecord1.setThreads(threadIdList);

        ThreadRecord record1 = new ThreadRecord();
        record1.setThreadId(randomUUID().toString());
        record1.setThreadTitle("thread1");
        record1.setUsers(users);
        record1.setMessages(messages);

        List<ThreadRecord> threadRecordList = new ArrayList<>();
        threadRecordList.add(record1);

        //WHEN
        when(threadRepository.findAll()).thenReturn(threadRecordList);
        when(userRepository.findById(any())).thenReturn(Optional.of(userRecord1));

        //THEN - exception is thrown
        Assertions.assertThrows(InvalidUserCountException.class, ()-> threadService.addNewThread(threads), "An InvalidUserCountException should have occurred for user count under 2.");

    }

    @Test
    void addNewThread_PreventsDuplicateThreads_throwsException() {
        //GIVEN - thread
        String threadId = randomUUID().toString();
        String threadTitle = "testThreadTitle";
        String userId1 = randomUUID().toString();
        String userId2 = randomUUID().toString();

        List<String> users = new ArrayList<>();
        users.add(userId1);
        users.add(userId2);
        List<String> messages = new ArrayList<>();
        messages.add("test message");
        Threads threads = new Threads(threadId, threadTitle, users, messages);

        List<String> threadIdList = new ArrayList<>();
        threadIdList.add(threads.getThreadId());

        UserRecord userRecord1 = new UserRecord();
        userRecord1.setUserId(userId1);
        userRecord1.setName("user1");
        userRecord1.setThreads(threadIdList);

        ThreadRecord record1 = new ThreadRecord();
        record1.setThreadId(threadId);
        record1.setThreadTitle("thread1");
        record1.setUsers(users);
        record1.setMessages(messages);

        List<ThreadRecord> threadRecordList = new ArrayList<>();
        threadRecordList.add(record1);

        //WHEN - thread add
        when(threadRepository.findAll()).thenReturn(threadRecordList);
        when(userRepository.findById(any())).thenReturn(Optional.of(userRecord1));

        //THEN - attempt to add duplicate thread, exception should be thrown
        Assertions.assertThrows(DuplicateThreadException.class, ()-> threadService.addNewThread(threads), "An exception should have been thrown when adding two identical threadId's.");
    }


    /** ------------------------------------------------------------------------
     *  ThreadService.findAllThreads
     *  ------------------------------------------------------------------------ **/

    @Test
    void findAllThreads_Success() {
        // GIVEN
        List<String> users = new ArrayList<>();
        users.add("Test user 1");
        users.add("Test user 2");

        List<String> messages = new ArrayList<>();
        messages.add("Test message");

        ThreadRecord record1 = new ThreadRecord();
        record1.setThreadId(randomUUID().toString());
        record1.setThreadTitle("thread1");
        record1.setUsers(users);
        record1.setMessages(messages);

        ThreadRecord record2 = new ThreadRecord();
        record2.setThreadId(randomUUID().toString());
        record2.setThreadTitle("thread2");
        record2.setUsers(users);
        record2.setMessages(messages);

        List<ThreadRecord> recordList = new ArrayList<>();
        recordList.add(record1);
        recordList.add(record2);
        when(threadRepository.findAll()).thenReturn(recordList);

        // WHEN
        List<Threads> threadsList = threadService.findAllThreads();

        // THEN
        Assertions.assertNotNull(threadsList, "The threads list is returned");
        Assertions.assertEquals(2, users.size(), "There are two threads");

        for (Threads threads : threadsList) {
            if (threads.getThreadId() == record1.getThreadId()) {
                Assertions.assertEquals(record1.getThreadId(), threads.getThreadId(), "The thread Id matches");
                Assertions.assertEquals(record1.getThreadTitle(), threads.getThreadTitle(), "The thread title matches");
                Assertions.assertEquals(record1.getUsers(), threads.getUsers(), "The user list matches");
                Assertions.assertEquals(record1.getMessages(), threads.getMessages(), "The message list matches");

            } else if (threads.getThreadId() == record2.getThreadId()) {
                Assertions.assertEquals(record2.getThreadId(), threads.getThreadId(), "The thread Id matches");
                Assertions.assertEquals(record2.getThreadTitle(), threads.getThreadTitle(), "The thread title matches");
                Assertions.assertEquals(record2.getUsers(), threads.getUsers(), "The user list matches");
                Assertions.assertEquals(record2.getMessages(), threads.getMessages(), "The message list matches");

            } else {
                assertTrue(false, "User returned that was not in the records!");
            }
        }
    }

    @Test
    void findAllThreads_returnsEmpty_throwsException() {
        //GIVEN - empty return to findAll() call
        when(threadRepository.findAll()).thenReturn(null);

        //WHEN - calling findAll()
        //THEN - exception is thrown
        Assertions.assertThrows(NullPointerException.class, ()-> threadService.findAllThreads(), "Exception should have thrown if findAll() returned null");
    }

    /** ------------------------------------------------------------------------
     *  ThreadService.findThreadById
     *  ------------------------------------------------------------------------ **/

    @Test
    void findByThreadId_Success() {
        // GIVEN
        String threadId = randomUUID().toString();
        List<String> users = new ArrayList<>();
        users.add("Test user");

        List<String> messages = new ArrayList<>();
        messages.add("Test message");

        ThreadRecord record = new ThreadRecord();
        record.setThreadId(threadId);
        record.setThreadTitle("testThread");
        record.setUsers(users);
        record.setMessages(messages);
        when(threadRepository.findById(threadId)).thenReturn(Optional.of(record));

        // WHEN
        Threads threads = threadService.findThreadByThreadId(threadId);

        // THEN
        Assertions.assertNotNull(threads, "The user is returned");
        Assertions.assertEquals(record.getThreadId(), threads.getThreadId(), "The thread id matches");
        Assertions.assertEquals(record.getThreadTitle(), threads.getThreadTitle(), "The thread title matches");
        Assertions.assertEquals(record.getUsers(), threads.getUsers(), "The users match");
        Assertions.assertEquals(record.getMessages(), threads.getMessages(), "The messages match");
    }

    /** ------------------------------------------------------------------------
     *  ThreadService.updateThread
     *  ------------------------------------------------------------------------ **/

    @Test
    void userService_successfullyUpdatesUser() {
        // GIVEN
        String threadId = randomUUID().toString();
        List<String> users = new ArrayList<>();
        users.add("Test user");

        List<String> messages = new ArrayList<>();
        messages.add("Test message");

        Threads threads = new Threads(threadId, "testThread", users, messages);

        when(threadRepository.existsById(threadId)).thenReturn(true);

        // WHEN
        threadService.updateThread(threads);

        // THEN
        verify(threadRepository, times(1)).save(any());
    }

    /** ------------------------------------------------------------------------
     *  ThreadService.updateThread
     *  ------------------------------------------------------------------------ **/

    @Test
    void ThreadService_deletesThread() throws Exception {
        // GIVEN
        String threadId = randomUUID().toString();
        List<String> users = new ArrayList<>();
        users.add("test user1");
        users.add("test user2");

        List<String> messages = new ArrayList<>();
        messages.add("Test message 1");
        messages.add("test message 2");

        List<String> threadsList = new ArrayList<>();
        threadsList.add(threadId);
        UserRecord user = new UserRecord();
        user.setUserId("userId");
        user.setName("name");
        user.setThreads(threadsList);

        Threads threads = new Threads(threadId, "testThread", users, messages);

        ThreadRecord threadRecord = new ThreadRecord();
        threadRecord.setThreadId("testThread");
        threadRecord.setThreadTitle("testName");
        threadRecord.setUsers(users);
        threadRecord.setMessages(messages);

        when(threadRepository.findById(any())).thenReturn(Optional.of(threadRecord));
        when(userRepository.findById((any()))).thenReturn(Optional.of(user));

        // WHEN
        threadService.deleteThread(threadId);

        // THEN
        verify(threadRepository, times(1)).deleteById(threadId);
    }
    @Test
    void deleteThread_notFound_throwsException() {
        //GIVEN
        String threadId = randomUUID().toString();
        List<String> users = new ArrayList<>();
        users.add("test user1");
        users.add("test user2");

        List<String> messages = new ArrayList<>();
        messages.add("Test message 1");
        messages.add("test message 2");

        ThreadRecord threadRecord = new ThreadRecord();
        threadRecord.setThreadId("testThread");
        threadRecord.setThreadTitle("testName");
        threadRecord.setUsers(users);
        threadRecord.setMessages(messages);

        when(threadRepository.findById(any())).thenReturn(Optional.of(threadRecord));
        when(threadService.findThreadByThreadId(threadId)).thenReturn(null);

        //WHEN - threadId not found
        //THEN - throws exception
        Assertions.assertThrows(NullPointerException.class, () -> threadService.deleteThread(threadId), "Exception should have been thrown if the threadId was not found.");
    }

}
