package com.edu.maxqaq.controller;


import com.edu.maxqaq.RabbitMQTest.Productor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author maxqaq
 * @since 2022-03-18
 */
@RestController
@RequestMapping("/maxqaq/user")
public class UserController {


    @Autowired
    Productor productor;

    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        productor.send("hello");
    }
}
