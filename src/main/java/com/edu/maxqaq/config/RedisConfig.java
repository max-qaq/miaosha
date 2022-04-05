package com.edu.maxqaq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @program: miaosha
 * @description: Redis配置类
 * @author: max-qaq
 * @create: 2022-04-05 11:44
 **/
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());//设置key序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());//设置value的序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());//hash key序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());//hash的value序列化

        //注入连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;

    }
}
