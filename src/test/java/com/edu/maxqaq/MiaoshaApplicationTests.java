package com.edu.maxqaq;

import com.edu.maxqaq.utils.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class MiaoshaApplicationTests {

    @Autowired
    UserUtil userUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript redisScript;

    @Test
    void contextLoads() throws SQLException, IOException, ClassNotFoundException {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        //不存在才能设置成功
        Boolean isLock = valueOperations.setIfAbsent("Lock1", value,5, TimeUnit.SECONDS);
        if (isLock){
            //获取锁
            /**
             * 进行操作
             */
            //释放锁
            redisTemplate.execute(redisScript, Collections.singletonList("Lock1"), value);
        }else{
            System.out.println("其他线程正在使用,请等待");
        }
    }

}
