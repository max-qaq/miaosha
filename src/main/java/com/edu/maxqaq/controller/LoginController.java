package com.edu.maxqaq.controller;

import com.edu.maxqaq.service.UserService;
import com.edu.maxqaq.vo.LoginVo;
import com.edu.maxqaq.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: miaosha
 * @description:
 * @author: max-qaq
 * @create: 2022-03-20 21:11
 **/
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {


    @Autowired
    UserService userService;
    /**
     * 跳转登录页面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }


    @ResponseBody
    @RequestMapping("/doLogin")
    public RespBean doLogin(LoginVo loginVo){
        log.info("{}",loginVo.getPassword());
        return userService.doLogin(loginVo);
    }
}
