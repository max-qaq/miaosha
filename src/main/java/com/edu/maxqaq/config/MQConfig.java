package com.edu.maxqaq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: miaosha
 * @description: RabbitMQ配置类
 * @author: max-qaq
 * @create: 2022-04-09 14:06
 **/
@Configuration
public class MQConfig {
    public static final String QUEUE = "secKillQueue";
    public static final String EXCHANGE = "secKillExchange";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }
}
