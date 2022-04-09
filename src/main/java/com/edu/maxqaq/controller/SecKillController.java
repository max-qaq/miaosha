package com.edu.maxqaq.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.maxqaq.entity.Order;
import com.edu.maxqaq.entity.SeckillOrder;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.mapper.SeckillOrderMapper;
import com.edu.maxqaq.service.GoodsService;
import com.edu.maxqaq.service.OrderService;
import com.edu.maxqaq.service.SeckillOrderService;
import com.edu.maxqaq.vo.GoodsVo;
import com.edu.maxqaq.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: miaosha
 * @description: 秒杀
 * @author: max-qaq
 * @create: 2022-04-06 10:28
 **/
@Controller
@RequestMapping("/secKill")
public class SecKillController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping("/doSecKill")
    public String doSecKill(Model model, User user,Long goodsId){
        if (null == user){
            //未登录 返回登录
            return "login";
        }
        model.addAttribute("user",user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() <= 0){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        //直接从redis获取
//        SeckillOrder secKillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
//                .eq("goods_id", goodsId));
        SeckillOrder secKillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getGoodsId());
        if (secKillOrder != null){
            //被抢购过了
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.secKill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
    }
}
