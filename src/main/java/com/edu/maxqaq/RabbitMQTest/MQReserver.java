package com.edu.maxqaq.RabbitMQTest;

import com.edu.maxqaq.config.MQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @program: miaosha
 * @description: mq消费者
 * @author: max-qaq
 * @create: 2022-04-09 14:17
 **/
@Service
@Slf4j
public class MQReserver {
    @RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("{}",msg);
    }
    @RabbitListener(queues = MQConfig.QUEUE01)
    public void receive01(Object msg){
        log.info("QUEUE01"+msg);
    }
    @RabbitListener(queues = MQConfig.QUEUE02)
    public void receive02(Object msg){
        log.info("QUEUE02"+msg);
    }
}
