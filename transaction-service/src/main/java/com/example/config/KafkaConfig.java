package com.example.config;

import com.example.kafka.TransactionPayload;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap.servers}")
    String bootstrapServers;

    @Bean
    public ConsumerFactory<String, TransactionPayload> txnCompConsumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new JsonDeserializer<>(TransactionPayload.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionPayload> txnCompKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionPayload> factory = new ConcurrentKafkaListenerContainerFactory<String, TransactionPayload>();
        factory.setConsumerFactory(txnCompConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, TransactionPayload> producerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String, TransactionPayload>(configs);
    }

    @Bean
    public KafkaTemplate<String, TransactionPayload> transactionInitKafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
