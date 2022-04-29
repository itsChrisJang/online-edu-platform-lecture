package com.example.onlineeduplatformlecture.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LectureProducerService {

    private static final Logger logger = LoggerFactory.getLogger(LectureProducerService.class);
    private static final String TOPIC = "lecture-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String title, String location) {
        logger.info(String.format("#### -> Producing message -> %s", title));
        logger.info(String.format("#### -> Producing message -> %s", location));
        this.kafkaTemplate.send(TOPIC, title + "/" + location);
    }
}
