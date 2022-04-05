package com.edu.maxqaq.controller;

import com.edu.maxqaq.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @program: miaosha
 * @description:
 * @author: max-qaq
 * @create: 2022-04-02 21:30
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @RequestMapping("/toList")
    public String toList(HttpSession session, Model model , @CookieValue("userTicket") String ticket){
        if (null == ticket){
            //没cookie,去登录
            return "login";
        }
        User user = (User) session.getAttribute(ticket);
        if (null == user){
            //session的用户为空,去登录
            return "login";
        }
        //在模型里面设置user
        model.addAttribute("user",user);
        return "goodsList";
    }
}
