package com.droppa.DroppaUserService.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.droppa.DroppaUserService.messaging.KafkaTopics;

@Configuration
@ConditionalOnProperty(name = "app.kafka.infrastructure-enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTopicConfiguration {

    @Bean
    NewTopic paymentCommandsTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_COMMANDS).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic paymentCommandsDltTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_COMMANDS_DLT).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic paymentEventsTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_EVENTS).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic notificationCommandsTopic() {
        return TopicBuilder.name(KafkaTopics.NOTIFICATION_COMMANDS).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic notificationCommandsDltTopic() {
        return TopicBuilder.name(KafkaTopics.NOTIFICATION_COMMANDS_DLT).partitions(3).replicas(1).build();
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory,
            KafkaTemplate<Object, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(record.topic() + "-dlt", record.partition())
        );
        factory.setCommonErrorHandler(new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 3L)));
        return factory;
    }
}
