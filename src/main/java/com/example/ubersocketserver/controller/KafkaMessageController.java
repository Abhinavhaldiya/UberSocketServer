package com.example.ubersocketserver.controller;

import com.example.ubersocketserver.kakfa.KafkaProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaMessageController {
    private final KafkaProducer kafkaProducer;

    public KafkaMessageController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send")
    public void sendMessageToKafka(@RequestBody String message) {
        kafkaProducer.sendMessage(message);
    }
}
