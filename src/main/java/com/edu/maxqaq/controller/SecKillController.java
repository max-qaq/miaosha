package com.edu.maxqaq.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.maxqaq.entity.Order;
import com.edu.maxqaq.entity.SeckillOrder;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.mapper.SeckillOrderMapper;
import com.edu.maxqaq.service.GoodsService;
import com.edu.maxqaq.service.OrderService;
import com.edu.maxqaq.service.RabbitMQ.MQSender;
import com.edu.maxqaq.service.SeckillOrderService;
import com.edu.maxqaq.utils.JsonUtil;
import com.edu.maxqaq.vo.GoodsVo;
import com.edu.maxqaq.vo.RespBean;
import com.edu.maxqaq.vo.RespBeanEnum;
import com.edu.maxqaq.vo.SecKillMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @program: miaosha
 * @description: 秒杀
 * @author: max-qaq
 * @create: 2022-04-06 10:28
 **/
@Controller
@RequestMapping("/secKill")
public class SecKillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    MQSender mqSender;

    @Autowired
    private RedisScript<Long> redisScript;
    private HashMap<Long,Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping("/doSecKill")
    public String doSecKill(Model model, User user,Long goodsId){
        if (null == user){
            //未登录 返回登录
            return "login";
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复抢购
        //直接从redis获取
        SeckillOrder secKillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (secKillOrder != null){
            //被抢购过了
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        if (!EmptyStockMap.get(goodsId)){
            return "orderDetail";
        }
        Long decrement = (Long) redisTemplate.execute(redisScript, Collections.singletonList("secKillGoods:"+goodsId),Collections.EMPTY_LIST);
        if (decrement <= 0){
            EmptyStockMap.put(goodsId,false);
            model.addAttribute("errmsg",RespBeanEnum.EMPTY_STOCK.getMessage());
        }
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //处理订单用RabbitMQ
        SecKillMessage secKillMessage = new SecKillMessage(user, goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(secKillMessage));


        Order order = orderService.secKill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
        /*
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
         */

    }

    //获取秒杀结果 有OrderId成功 -1失败 0排队中
    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if (null == user){
            return RespBean.error(RespBeanEnum.ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }
    /**
     * 初始化,把商品库存加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(goodsVo)) return;
        goodsVo.forEach(goodsVo1 -> {
            EmptyStockMap.put(goodsVo1.getGoodsId(),true);//有库存
            redisTemplate.opsForValue().set("secKillGoods:"+goodsVo1.getGoodsId(),goodsVo1.getStockCount());
        });
    }
}
