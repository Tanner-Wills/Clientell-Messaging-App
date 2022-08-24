package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.*;
import com.kenzie.appserver.service.ThreadService;
import com.kenzie.appserver.service.model.Threads;

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
@RequestMapping("/Threads")
public class ThreadController {

    private ThreadService threadService;

    ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<List<String>> getThreadMessages(@PathVariable("threadId") String threadId)
            throws Exception {
        List<String> messages = threadService.viewThreadMessages(threadId);
        // if there are no users, return a 204
        if (messages == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<ThreadResponse> addNewThread(@RequestBody ThreadCreateRequest threadCreateRequest)
            throws Exception {
        List<String> messages = new ArrayList<>();
        Threads threads = new Threads(randomUUID().toString(),
                threadCreateRequest.getThreadTitle(),
                threadCreateRequest.getUsers(),
                messages);
        threadService.addNewThread(threads);

        ThreadResponse threadResponse = createThreadResponse(threads);

        return ResponseEntity.created(URI.create("/Threads/" + threadResponse.getThreadId())).body(threadResponse);
    }

//    @PutMapping
//    public ResponseEntity<ThreadResponse> updateThread(@RequestBody Threads threadUpdate) {
//        Threads thread = new Threads(threadUpdate.getThreadId(),
//                threadUpdate.getThreadTitle(),
//                threadUpdate.getUsers(),
//                threadUpdate.getMessages());
//        threadService.updateThread(thread);
//
//        ThreadResponse threadResponse = createThreadResponse(thread);
//
//        return ResponseEntity.ok(threadResponse);
//    }

    @GetMapping
    public ResponseEntity<List<ThreadResponse>> getAllThreads() {
        List<Threads> threads = threadService.findAllThreads();

        // If there are no users, then return a 204
        if (threads == null ||  threads.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Otherwise, convert the List of Concert objects into a List of ConcertResponses and return it
        List<ThreadResponse> response = new ArrayList<>();
        for (Threads threads1 : threads) {
            response.add(this.createThreadResponse(threads1));
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{threadId}")
    public ResponseEntity deleteThreadById(@PathVariable("threadId") String threadId) {
        threadService.deleteThread(threadId);
        return ResponseEntity.noContent().build();
    }

    // Helper method to create a ThreadResponse from a Threads
    private ThreadResponse createThreadResponse(Threads threads) {
        ThreadResponse threadResponse = new ThreadResponse();
        threadResponse.setThreadId(threads.getThreadId());
        threadResponse.setThreadTitle(threads.getThreadTitle());
        threadResponse.setUsers(threads.getUsers());
        threadResponse.setMessages(threads.getMessages());
        return threadResponse;
    }
}
