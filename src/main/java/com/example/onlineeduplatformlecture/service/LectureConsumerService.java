package com.example.onlineeduplatformlecture.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LectureConsumerService {

    private final Logger logger = LoggerFactory.getLogger(LectureConsumerService.class);

    // topics  : Producer의 TOPIC Naming 일치
    // groupId : yml에서 group-id와 Naming 일치
    @KafkaListener(topics = "lecture-topic", groupId = "lecture-topic")
    public void consume(String message) {
        logger.info(String.format("#### -> Consumed message -> %s", message));
    }
}
