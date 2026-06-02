package com.example.ubersocketserver.controller;

import com.example.ubersocketserver.dto.ChatRequest;
import com.example.ubersocketserver.dto.ChatResponse;
import com.example.ubersocketserver.dto.TestRequest;
import com.example.ubersocketserver.dto.TestResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {
    private final SimpMessagingTemplate messagingTemplate;

    public TestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/ping")
    @SendTo("/topic/ping")
    public TestResponse pingCheck(TestRequest message){
        System.out.println("Received message from client " + message.getData());
        return TestResponse.builder().data("Received").build();
    }

    @MessageMapping("/chat/{room}")
    @SendTo("/topic/message/{room}")
    public ChatResponse chatMessage(ChatRequest request, @DestinationVariable("room") String room){
        return ChatResponse.builder()
                .name(request.getName())
                .message(request.getMessage())
                .timeStamp(""+System.currentTimeMillis())
                .build();
    }

    @MessageMapping("/privateChat/{room}/{userId}")
    public void privateChatMessage(@DestinationVariable String room, @DestinationVariable String userId, ChatRequest request) {
        ChatResponse response =  ChatResponse.builder()
                .name(request.getName())
                .message(request.getMessage())
                .timeStamp("" + System.currentTimeMillis())
                .build();
        messagingTemplate.convertAndSend("/queue/privateMessage/" + room + "/" + userId, response);
    }
}
