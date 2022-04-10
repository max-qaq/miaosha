package com.edu.maxqaq.service.RabbitMQ;

import com.edu.maxqaq.config.MQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: miaosha
 * @description:MQ发送
 * @author: max-qaq
 * @create: 2022-04-10 09:24
 **/
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSecKillMessage(String msg){
        log.info("发送秒杀信息:"+msg);
        rabbitTemplate.convertAndSend(MQConfig.EXCHANGE,"seckill.message",msg);
    }
}
