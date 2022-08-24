package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.MessageRepository;
import com.kenzie.appserver.repositories.model.MessageRecord;
import com.kenzie.appserver.service.model.Message;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
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
        messageRepository.save(createMessageRecord(message));
        return message;
    }

    public Message findMessageByMessageId(String messageId) {
        // find the user
        if (messageId == null) {
            throw new NullPointerException("MessageId cannot be null.");
        }
        Message messageFromBackendService = messageRepository.findById(messageId)
                .map(message -> new Message(message.getMessageId(),
                        message.getTimeStamp(),
                        message.getThreadId(),
                        message.getSender(),
                        message.getMessage()))
                .orElse(null);
        // return user
        return messageFromBackendService;
    }

    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }

    /**
     * Helper Methods
     */

    private MessageRecord createMessageRecord(Message message) {
        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setMessageId(message.getMessageId());
        messageRecord.setTimeStamp(message.getTimeStamp());
        messageRecord.setThreadId(message.getThreadId());
        messageRecord.setSender(message.getSender());
        messageRecord.setMessage(message.getMessage());
        return messageRecord;
    }
}
