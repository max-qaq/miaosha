package com.edu.maxqaq.service.RabbitMQ;

import com.edu.maxqaq.config.MQConfig;
import com.edu.maxqaq.entity.SeckillOrder;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.service.GoodsService;
import com.edu.maxqaq.service.OrderService;
import com.edu.maxqaq.utils.JsonUtil;
import com.edu.maxqaq.vo.GoodsVo;
import com.edu.maxqaq.vo.RespBeanEnum;
import com.edu.maxqaq.vo.SecKillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @program: miaosha
 * @description: MQ消费
 * @author: max-qaq
 * @create: 2022-04-10 09:33
 **/
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String msg){
        log.info("接收到的消息:"+msg);
        SecKillMessage secKillMessage = JsonUtil.jsonStr2Object(msg, SecKillMessage.class);
        Long goodsId = secKillMessage.getGoodsId();
        User user = secKillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1){
            return;
        }
        //判断是否重复抢购
        //直接从redis获取
        SeckillOrder secKillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (secKillOrder != null){
            //被抢购过了
           return;
        }
        //下单
        orderService.secKill(user,goodsVo);

    }
}
