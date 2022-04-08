package com.edu.maxqaq;

import com.edu.maxqaq.utils.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.sql.SQLException;


@SpringBootTest
class MiaoshaApplicationTests {

    @Autowired
    UserUtil userUtil;
    @Test
    void contextLoads() throws SQLException, IOException, ClassNotFoundException {
        userUtil.createUser(5000);
    }

}
