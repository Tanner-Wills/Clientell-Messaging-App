package com.kenzie.appserver.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.MessageCreateRequest;
import com.kenzie.appserver.controller.model.MessageResponse;
import com.kenzie.appserver.service.MessageService;
import com.kenzie.appserver.service.model.Message;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class MessageControllerIntegrationTest {

    private static final String MESSAGE_ID = UUID.randomUUID().toString();
    private static final String TIMESTAMP = UUID.randomUUID().toString();
    private static final String THREAD_ID = UUID.randomUUID().toString();
    private static final String SENDER = "Gandalf";
    private static final String TEST_MESSAGE = "Fly you fools!";

    @Autowired
    private MockMvc mvc;

    @Autowired
    MessageService messageService;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();

    /** ------------------------------------------------------------------------
     *  messageController.createMessage @PostMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    void createMessage_createSuccess_returnsMessage() throws Exception {
        //GIVEN
        MessageCreateRequest request = new MessageCreateRequest();
        request.setThreadId(THREAD_ID);
        request.setSender(SENDER);
        request.setMessage(TEST_MESSAGE);

        Message expectedMessage = new Message(null, null, THREAD_ID, SENDER, TEST_MESSAGE);

        //WHEN
        String response = mvc.perform(
                        post("/Messages")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andExpect(jsonPath("messageId")
                        .exists())
                .andExpect(jsonPath("timeStamp")
                        .exists())
                .andExpect(jsonPath("threadId")
                        .exists())
                .andExpect(jsonPath("sender")
                        .exists())
                .andExpect(jsonPath("message")
                        .exists())
                .andExpect(status().is(201))
                .andReturn().getResponse().getContentAsString();

        MessageResponse result = mapper.readValue(response, new TypeReference<MessageResponse>() {});
        expectedMessage.setMessageId(result.getMessageId());
        expectedMessage.setTimeStamp(result.getTimeStamp());

        String getRequest = mvc.perform(
                        get("/Messages/{messageId}", expectedMessage.getMessageId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MessageResponse getResult = mapper.readValue(getRequest, new TypeReference<MessageResponse>() {});

        //THEN
        Assertions.assertNotNull(getResult);
        Assertions.assertEquals(getResult.getMessageId(), expectedMessage.getMessageId());
        Assertions.assertEquals(getResult.getTimeStamp(), expectedMessage.getTimeStamp());
        Assertions.assertEquals(getResult.getThreadId(), expectedMessage.getThreadId());
        Assertions.assertEquals(getResult.getSender(), expectedMessage.getSender());
        Assertions.assertEquals(getResult.getMessage(), expectedMessage.getMessage());
    }

    @Test
    void createMessage_missingFields_throwsException() throws Exception {
        //GIVEN
        MessageCreateRequest request = new MessageCreateRequest();

        //WHEN
        //THEN
        Assertions.assertThrows(NestedServletException.class, () ->
                mvc.perform(
                        post("/Messages")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))));
    }

    /** ------------------------------------------------------------------------
     *  messageController.getMessageById @GetMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    void getMessageById_found_returnsMessage() throws Exception {
        //GIVEN
        Message message = new Message(MESSAGE_ID, TIMESTAMP, THREAD_ID, SENDER, TEST_MESSAGE);

        Message expectedMessage = messageService.addNewMessage(message);

        //WHEN
        String getRequest = mvc.perform(
                        get("/Messages/{messageId}", expectedMessage.getMessageId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MessageResponse getResult = mapper.readValue(getRequest, new TypeReference<MessageResponse>() {});

        //THEN
        Assertions.assertNotNull(getResult);
        Assertions.assertEquals(getResult.getMessageId(), expectedMessage.getMessageId());
        Assertions.assertEquals(getResult.getTimeStamp(), expectedMessage.getTimeStamp());
        Assertions.assertEquals(getResult.getThreadId(), expectedMessage.getThreadId());
        Assertions.assertEquals(getResult.getSender(), expectedMessage.getSender());
        Assertions.assertEquals(getResult.getMessage(), expectedMessage.getMessage());
    }

    @Test
    void getMessageById_messageNotFound_returns404() throws Exception {
        //GIVEN
        Message expectedMessage = new Message(MESSAGE_ID, TIMESTAMP, THREAD_ID, SENDER, TEST_MESSAGE);

        //WHEN
        //THEN
        mvc.perform(
                get("/Messages/{messageId}", expectedMessage.getMessageId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    /** ------------------------------------------------------------------------
     *  messageController.deleteMessageById @DeleteMapping
     *  ------------------------------------------------------------------------ **/

    @Test
    void deleteMessageById_deletesMessage() throws Exception {
        //GIVEN
        Message message = new Message(MESSAGE_ID, TIMESTAMP, THREAD_ID, SENDER, TEST_MESSAGE);
        messageService.addNewMessage(message);
        Assertions.assertNotNull(messageService.findMessageByMessageId(message.getMessageId()));

        //WHEN
        mvc.perform(
                delete("/Messages/{messageId}", MESSAGE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

        //THEN
        Assertions.assertNull(messageService.findMessageByMessageId(message.getMessageId()));
    }

    @Test
    void deleteMessageById_userNotFound() {
        //GIVEN
        Message expectedMessage = new Message(MESSAGE_ID, TIMESTAMP, THREAD_ID, SENDER, TEST_MESSAGE);

        //WHEN
        //THEN
        Assertions.assertThrows(NestedServletException.class, () ->
                mvc.perform(
                        delete("/Messages/{messageId}", expectedMessage.getMessageId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204)));
    }
}
