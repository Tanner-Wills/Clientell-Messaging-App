package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.MessageRepository;
import com.kenzie.appserver.repositories.model.MessageRecord;
import com.kenzie.appserver.service.model.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.Mockito.*;

import static java.util.UUID.randomUUID;

public class MessageServiceTest {

    private MessageRepository messageRepository;
    private MessageService messageService;

    @BeforeEach
    void setup() {
        messageRepository = mock(MessageRepository.class);
        messageService = new MessageService(messageRepository);
    }

    /** ------------------------------------------------------------------------
     *  messageService.addNewMessage
     *  ------------------------------------------------------------------------ **/

    @Test
    void addNewMessage_savesMessage_returnsMessage() throws Exception {
        //GIVEN
        String messageId = randomUUID().toString();
        String timestamp = LocalDateTime.now().toString();
        String threadId = randomUUID().toString();
        String sender = "sender";
        String recipient = "recipient";
        String messageContent = "test message";

        Message message = new Message(messageId, timestamp, threadId, sender, messageContent);

        ArgumentCaptor<MessageRecord> messageRecordArgumentCaptor = ArgumentCaptor.forClass(MessageRecord.class);

        //WHEN
        Message result = messageService.addNewMessage(message);

        //THEN
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
    void addNewMessage_nullMessage_throwsException() {
        //GIVEN - null message
        Message message = null;

        //WHEN - adding null message
        //THEN - exception is thrown
        Assertions.assertThrows(NullPointerException.class, () -> messageService.addNewMessage(message));
    }

    /** ------------------------------------------------------------------------
     *  messageService.findMessageByMessageId
     *  ------------------------------------------------------------------------ **/

    @Test
    void findMessageByMessageId_returnsMessage() {
        //GIVEN - valid message
        String messageId = randomUUID().toString();
        String timestamp = LocalDateTime.now().toString();
        String threadId = randomUUID().toString();
        String sender = "sender";
        String recipient = "recipient";
        String messageContent = "test message";

        Message message = new Message(messageId, timestamp, threadId, sender, messageContent);

        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setMessageId(messageId);
        messageRecord.setTimeStamp(timestamp);
        messageRecord.setThreadId(threadId);
        messageRecord.setSender(sender);
        messageRecord.setMessage(messageContent);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageRecord));

        //WHEN - retrieving message with messageId
        Message result = messageService.findMessageByMessageId(messageId);

        //THEN - message is received
        Assertions.assertEquals(message.getMessageId(), result.getMessageId());
        Assertions.assertEquals(message.getTimeStamp(), result.getTimeStamp());
        Assertions.assertEquals(message.getThreadId(), result.getThreadId());
        Assertions.assertEquals(message.getSender(), result.getSender());
        Assertions.assertEquals(message.getMessage(), result.getMessage());
    }
    @Test
    void findMessageByMessageId_nullMessageId_throwsException() {
        //GIVEN - null messageId
        String messageId = null;

        //WHEN - search is executed with null messageId
        //THEN - exception is thrown
        Assertions.assertThrows(NullPointerException.class, () -> messageService.findMessageByMessageId(messageId));
    }

    /** ------------------------------------------------------------------------
     *  messageService.deleteMessage
     *  ------------------------------------------------------------------------ **/

    @Test
    void deleteMessage_withMessageId_deletesMessage() {
        //GIVEN
        String messageId = randomUUID().toString();

        //WHEN
        messageRepository.deleteById(messageId);

        //THEN
        verify(messageRepository,times(1)).deleteById(messageId);

    }
}
