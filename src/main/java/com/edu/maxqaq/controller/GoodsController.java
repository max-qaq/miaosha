package com.edu.maxqaq.controller;

import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.service.GoodsService;
import com.edu.maxqaq.service.UserService;
import com.edu.maxqaq.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList (Model model ,User user,HttpServletRequest httpServletRequest,
                          HttpServletResponse httpServletResponse){
//        if (null == userTicket){
//            //没cookie,去登录
//            return "login";
//        }
////        User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByCookie(userTicket, request, response);

        //从缓存取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        //在模型里面设置user
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        //如果html为空,存入redis
        WebContext webContext = new WebContext(httpServletRequest,httpServletResponse,httpServletRequest.getServletContext(),
                httpServletRequest.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",webContext);
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60, TimeUnit.MINUTES);
        }
        return html;
    }

    /**
     * 调转商品详情页
     * @param goodsId
     * @return
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model,User user, @PathVariable Long goodsId){
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //倒计时
        int remainSeconds = 0;
        //未开始
        if (nowDate.before(startDate)){
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        }else if (nowDate.after(endDate)){
            //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            //进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",goodsVo);
        log.info("{}",goodsService.findGoodsVoByGoodsId(goodsId));
        return "goodsDetail";
    }
}
