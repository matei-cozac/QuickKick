package org.notificationservice.kafka.consumer;

import avro.Notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final EmailService emailService;

    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }
    @KafkaListener(topics = "notifications_register", groupId = "quick-kick-group")
    public void listen(ConsumerRecord<String, Notification> record) {

        try {
            Notification notification = record.value();
            emailService.sendRegisterMail(notification);
            logger.info(record.toString());
        } catch(Exception e) {
            logger.error("Received booking: {}", e.getMessage());
        }
    }
}
