package com.objectpartners.buesing.map;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    final private KafkaProperties properties;

    public KafkaConfig(KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    public ConsumerFactory<String, SpecificRecord> kafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties());
    }

    //TODO figure out how to level the auto configuration of spring more.
    @Bean
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer
    ) {

        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(kafkaConsumerFactory());

        ContainerProperties containerProperties = factory.getContainerProperties();

        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> kafkaConsumerFactory2() {

        Map<String, Object> map = properties.buildConsumerProperties();

        //org.apache.kafka.connect.json.JsonDeserializer
        //org.apache.kafka.common.serialization.StringDeserializer

        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);

//        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
//        properties.setProperty("value.deserializer", KafkaAvroDeserializer.class.getName());
//        properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");
//        properties.setProperty("specific.avro.reader", "true");
//
        return new DefaultKafkaConsumerFactory<>(map);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory2(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer
    ) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(kafkaConsumerFactory2());

        ContainerProperties containerProperties = factory.getContainerProperties();

        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL);



        return factory;
    }

    @Bean
    public ConsumerFactory<String, GenericRecord> kafkaConsumerFactory3() {

        Map<String, Object> map = properties.buildConsumerProperties();

        //org.apache.kafka.connect.json.JsonDeserializer
        //org.apache.kafka.common.serialization.StringDeserializer

       // map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);

//        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
//        properties.setProperty("value.deserializer", KafkaAvroDeserializer.class.getName());
//        properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");
        map.put("specific.avro.reader", "false");
//
        return new DefaultKafkaConsumerFactory<>(map);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GenericRecord> kafkaListenerContainerFactory3(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer
    ) {

        ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(kafkaConsumerFactory3());

        ContainerProperties containerProperties = factory.getContainerProperties();

        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL);


        return factory;
    }

//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> kafkaListenerContainerFactory() {
//
//        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(kafkaConsumerFactory());
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<String, SpecificRecord> kafkaConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties());
//    }
//
////    @Bean
////    public ConcurrentKafkaListenerContainerFactoryConfigurer kafkaListenerContainerFactoryConfigurer() {
////        ConcurrentKafkaListenerContainerFactoryConfigurer configurer = new ConcurrentKafkaListenerContainerFactoryConfigurer();
////        configurer.setKafkaProperties(this.properties);
////        configurer.setMessageConverter(this.messageConverter);
////        configurer.setReplyTemplate(this.kafkaTemplate);
////        return configurer;
////    }
//
////    @Bean
////    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
////            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
////            ConsumerFactory<String, SpecificRecord> kafkaConsumerFactory) {
////        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
////        configurer.configure(factory, kafkaConsumerFactory);
////        return factory;
////    }
//
//    @Bean
//    public KafkaListenerContainerFactory<?> otherKafkaListenerContainerFactory(){
//        ConcurrentKafkaListenerContainerFactory<String,SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(kafkaConsumerFactory());
//        factory.setConcurrency(3);
//        factory.setAutoStartup(true);
//        return factory;
//    }
////
////    @Bean
////    public ConsumerFactory<String,String> otherConsumerFactory(){
////        return new DefaultKafkaConsumerFactory<>(otherConsumerConfigs());
////    }
////
////    @Bean
////    Map<String, Object> otherConsumerConfigs() {
////        Map<String, Object> map = properties.buildConsumerProperties();
////
////        System.out.println(">>>");
////        System.out.println(">>>");
////        System.out.println(">>>");
////        System.out.println(map);
////        System.out.println(">>>");
////        System.out.println(">>>");
////        System.out.println(">>>");
////
////        return map;
////    }
//
////    @Autowired
////    private ConsumerFactory<String, ?> consumerFactory;
////
////    @Bean
////    public ConsumerFactory<String, Integer> otherConsumerFactory() {
////        Map<String, Object> props = new HashMap<>();
////        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
////        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
////        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
////        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
////        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new IntegerDeserializer());
////    }
//
//
//    @Bean
//    public ConsumerFactory<?, ?> kafkaConsumerFactory2() {
//
//        Map<String, Object> map = this.properties.buildAdminProperties();
//
//        System.out.println(map);
//
//        return new DefaultKafkaConsumerFactory<>(map);
//    }
//

}
