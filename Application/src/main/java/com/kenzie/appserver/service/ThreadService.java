package com.kenzie.appserver.service;

import com.kenzie.appserver.Exceptions.DuplicateThreadException;
import com.kenzie.appserver.Exceptions.InvalidUserCountException;
import com.kenzie.appserver.repositories.ThreadRepository;
import com.kenzie.appserver.repositories.model.ThreadRecord;

import com.kenzie.appserver.service.model.Message;
import com.kenzie.appserver.service.model.Threads;
import com.kenzie.appserver.service.model.User;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    private ThreadRepository threadRepository;
    private MessageService messageService;
    private UserService userService;

    @Autowired
    public ThreadService(ThreadRepository threadRepository,
                         MessageService messageService,
                         UserService userService) {
        this.threadRepository = threadRepository;
        this.messageService = messageService;
        this.userService = userService;
    }

    /**
     * CRUD methods for Threads
     */

    public List<Threads> findAllThreads() {
        List<Threads> threads = new ArrayList<>();

        Iterable<ThreadRecord> threadIterator = threadRepository.findAll();
        if (threadIterator == null) {
            throw new NullPointerException("No threads found in database.");
        }

        for (ThreadRecord record : threadIterator) {
            threads.add(new Threads(record.getThreadId(),
                    record.getThreadTitle(),
                    record.getUsers(),
                    record.getMessages()));
        }
        return threads;
    }

    public Threads findThreadByThreadId(String threadId) {
        // find the user
        Threads threadFromBackendService = threadRepository.findById(threadId)
                .map(thread -> new Threads(thread.getThreadId(),
                        thread.getThreadTitle(),
                        thread.getUsers(),
                        thread.getMessages()))
                .orElse(null);

        // return user
        return threadFromBackendService;
    }


    public Threads addNewThread(Threads threads) throws Exception {
        if (threads == null) {
            throw new NullPointerException("Can't create a null thread!");
        }

        // check to see if users List includes two users, if not throw exception
        checkThreadUserList(threads);

        // check to see if the threadId already exists in the Threads List
        checkThreadListForDuplicates(threads);

        // update users with the new threadId
        updateUsersWithNewThreadId(threads);

        threadRepository.save(createThreadRecord(threads));
        return threads;
    }

    public void updateThread(Threads threads) {
        threadRepository.save(createThreadRecord(threads));
    }

    public void deleteThread(String threadId) {
        // first, delete all messages
        try {
            Threads threads = findThreadByThreadId(threadId);
            List<String> messageList = threads.getMessages();

            for (String messageIdFromThread : messageList) {
                messageService.deleteMessage(messageIdFromThread);
            }

            // second, delete threadId from users
            deleteThreadIdFromUsers(threads);

            // third, delete thread
            threadRepository.deleteById(threadId);

        } catch (Exception e) {
            throw new NullPointerException("Thread was not found in the database.");
        }
    }


    /**
     * CRUD methods for Messages
     *
     * Message is a child class of the Threads object.
     * To create a message, you must input a valid Message object.
     *
     * Individual messages cannot be searched for.
     * Instead, Messages can be viewed by searching for the ThreadId parent object
     *
     * Individual messages cannot be deleted.
     * Instead, Deleting a Thread will delete all the messages associated with it.
     *
     * Messages cannot be updated.
     *
     */

    public Message addNewMessage(Message message) throws Exception {
        if (message == null) {
            throw new NullPointerException("Can't create a null message!");
        }
        checkIfUserIsInThreads(message);
        messageService.addNewMessage(message);
        updateThreadWithNewMessageId(message);
        return message;
    }

    public List<String> viewThreadMessages(String threadId) throws Exception {
        if (threadId == null) {
            throw new NullPointerException("Need a valid threadId!");
        }
        Threads threads = findThreadByThreadId(threadId);
        //            Message message = messageService.findMessageByMessageId(messageId);
        //            String text = message.getMessage();
        //            String sender = userService.findUserByUserId(message.getSender()).getName();
        //            messages.add(sender + ":");
        //            messages.add(text);
        return new ArrayList<>(threads.getMessages());
    }

    /**
     * Helper methods
     */
    private void checkIfUserIsInThreads(Message message) throws Exception {
        Threads threads = findThreadByThreadId(message.getThreadId());
        if (threads.getUsers().contains(message.getSender())) {
            throw new InvalidUserCountException("User is not in that thread!");
        }
    }


    private void checkThreadUserList(Threads threads) throws Exception {
        if (threads.getUsers().size() < 2) {
            throw new InvalidUserCountException("A new thread must have 2 users!");
        }
    }

    private void updateUsersWithNewThreadId(Threads threads) throws Exception {
        String threadId = threads.getThreadId();
        for (String userId: threads.getUsers()) {
            User newThreadForUser = userService.findUserByUserId(userId);
            List<String> userThreads = newThreadForUser.getThreads();
            userThreads.add(threadId);
            newThreadForUser.setThreads(userThreads);
            userService.updateUser(newThreadForUser);
        }
    }

    private void updateThreadWithNewMessageId(Message message) {
        String threadId = message.getThreadId();
        Threads threads = findThreadByThreadId(threadId);

        if (threads == null) {
            return;
        }

        List<String> messages = threads.getMessages();
        messages.add(message.getMessageId());
        threads.setMessages(messages);
        updateThread(threads);
    }

    private void checkThreadListForDuplicates(Threads threads) throws Exception {
        List<Threads> allThreads = findAllThreads();
        String threadsId = threads.getThreadId();
        for (Threads threadCheck : allThreads) {
            if (threadCheck.getThreadId().equals(threadsId)) {
                throw new DuplicateThreadException("Thread already exists in the List!");
            }
        }
    }

    private ThreadRecord createThreadRecord(Threads threads) {
        ThreadRecord threadRecord = new ThreadRecord();
        threadRecord.setThreadId(threads.getThreadId());
        threadRecord.setThreadTitle(threads.getThreadTitle());
        threadRecord.setUsers(threads.getUsers());
        threadRecord.setMessages(threads.getMessages());
        return threadRecord;
    }

    private void deleteThreadIdFromUsers(Threads threads) throws Exception {
        for (String userId : threads.getUsers()) {
            User user = userService.findUserByUserId(userId);
            List<String> userThreads = user.getThreads();
            userThreads.remove(threads.getThreadId());
            user.setThreads(userThreads);
            userService.updateUser(user);
        }
    }
}
