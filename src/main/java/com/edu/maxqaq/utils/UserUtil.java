package com.edu.maxqaq.utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.mapper.UserMapper;
import com.edu.maxqaq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: miaosha
 * @description: 生成用户工具类
 * @author: max-qaq
 * @create: 2022-04-06 20:17
 **/
@Component
public class UserUtil {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;

    public  void createUser(int count) throws SQLException, ClassNotFoundException, IOException {
        List<User> users = new ArrayList<>();
        FileWriter fw = new FileWriter("D:/desktop/data.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < count; i++){
            User user = new User();
            long perfix = 13000000000L;
            user.setId(perfix+i + 6000);
            user.setNickname("user"+i);
            user.setSalt("1A2B3C4D");
            user.setPassword(MD5Util.inputPassToDBpass("123456",user.getSalt()));
            users.add(user);
            userMapper.insert(user);
            Long userId = user.getId();
            String ticket = UUIDUtil.uuid();
            redisTemplate.opsForValue().set("user:" + ticket,user);
            bw.write(""+userId + "," + ticket + "\t\n");
            bw.flush();
        }
        bw.close();
        //插入数据库
    }
}
