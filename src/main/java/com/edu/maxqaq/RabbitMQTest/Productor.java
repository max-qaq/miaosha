package com.edu.maxqaq.RabbitMQTest;

import com.edu.maxqaq.config.MQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: miaosha
 * @description:mq生产
 * @author: max-qaq
 * @create: 2022-04-09 14:16
 **/
@Service
@Slf4j
public class Productor {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send(Object msg){
        log.info("发送消息{}",msg);
        rabbitTemplate.convertAndSend(MQConfig.EXCHANGE,"",msg);
    }
}
