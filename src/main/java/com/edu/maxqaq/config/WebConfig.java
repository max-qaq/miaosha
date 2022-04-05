package com.edu.maxqaq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @program: miaosha
 * @description: MVC配置类
 * @author: max-qaq
 * @create: 2022-04-05 15:48
 **/
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //自定义参数解析器
        resolvers.add(userArgumentResolver);
    }
}
