package com.edu.maxqaq.controller;

import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.service.GoodsService;
import com.edu.maxqaq.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @program: miaosha
 * @description:
 * @author: max-qaq
 * @create: 2022-04-02 21:30
 **/
@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    UserService userService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/toList")
    public String toList (Model model ,User user){
//        if (null == userTicket){
//            //没cookie,去登录
//            return "login";
//        }
////        User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByCookie(userTicket, request, response);
        if (null == user){
            //session的用户为空,去登录
            return "login";
        }
        //在模型里面设置user
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        log.info("{}",goodsService.findGoodsVo());
        return "goodsList";
    }

    /**
     * 调转商品详情页
     * @param goodsId
     * @return
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model,User user, @PathVariable Long goodsId){
        model.addAttribute("user",user);
        model.addAttribute("goods",goodsService.findGoodsVoByGoodsId(goodsId));
        log.info("{}",goodsService.findGoodsVoByGoodsId(goodsId));
        return "goodsDetail";
    }
}
