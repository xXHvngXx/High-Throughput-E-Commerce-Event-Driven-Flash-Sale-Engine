package com.flashsale.core_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration 
public class RabbitMQConfig {
    public static final String ORDER_QUEUE = "flashsale.order.queue";
    @Bean
    public Queue ordQueue(){
        return new Queue(ORDER_QUEUE, true);
    }
    @Bean
    public MessageConverter joinMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
