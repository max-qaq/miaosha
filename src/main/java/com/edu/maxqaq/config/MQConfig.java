package com.edu.maxqaq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
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

    public static final String QUEUE01 = "queue_fanout01";
    public static final String QUEUE02 = "queue_fanout02";
    public static final String EXCHANGE = "fanout_exchange";
    @Bean
    public Queue queue(){
        return new Queue("queue",true);
    }

    @Bean
    public Queue queue01(){
        return new Queue(QUEUE01);
    }
    @Bean
    public Queue queue02(){
        return new Queue(QUEUE02);
    }
    //交换机
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(EXCHANGE);
    }
    //绑定
    @Bean
    public Binding binding01(){
        return BindingBuilder.bind(queue01()).to(fanoutExchange());
    }
    @Bean
    public Binding binding02(){
        return BindingBuilder.bind(queue02()).to(fanoutExchange());
    }

}
