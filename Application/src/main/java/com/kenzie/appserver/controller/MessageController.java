package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.MessageCreateRequest;
import com.kenzie.appserver.controller.model.MessageResponse;
import com.kenzie.appserver.service.MessageService;
import com.kenzie.appserver.service.ThreadService;
import com.kenzie.appserver.service.model.Message;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URI;

import java.time.ZonedDateTime;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/Messages")
public class MessageController {

    private ThreadService threadService;
    private MessageService messageService;

    MessageController(ThreadService threadService, MessageService messageService) {
        this.threadService = threadService;
        this.messageService = messageService;
    }

    /**
     * CreateMessage
     * This endpoint will POST to the Messages table.
     * It will update the Threads table with the new messageId
     */

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@RequestBody MessageCreateRequest messageCreateRequest)
            throws Exception {
        Message message = new Message(randomUUID().toString(),
                ZonedDateTime.now().toString(),
                messageCreateRequest.getThreadId(),
                messageCreateRequest.getSender(),
                messageCreateRequest.getMessage());

        // Update thread with new messageId
        threadService.addNewMessage(message);

        MessageResponse messageResponse = createMessageResponse(message);

        return ResponseEntity.created(URI.create("/Messages/" + messageResponse.getMessageId())).body(messageResponse);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable("messageId") String messageId) {
        Message message = messageService.findMessageByMessageId(messageId);
        // if there are no users, return a 204
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        // Otherwise, convert it into a MessageResponse and return it
        MessageResponse messageResponse = createMessageResponse(message);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity deleteMessageById(@PathVariable("messageId") String messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper Methods
     */

    private MessageResponse createMessageResponse(Message message) {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessageId(message.getMessageId());
        messageResponse.setTimeStamp(message.getTimeStamp());
        messageResponse.setThreadId(message.getThreadId());
        messageResponse.setSender(message.getSender());
        messageResponse.setMessage(message.getMessage());
        return messageResponse;
    }
}
