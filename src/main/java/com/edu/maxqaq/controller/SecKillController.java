package com.edu.maxqaq.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.maxqaq.Exception.GlobalException;
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
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @RequestMapping("/{path}/doSecKill2")
    @ResponseBody
    public RespBean doSecKill2(Model model, User user, Long goodsId, @PathVariable String path){
        if (null == user){
            //未登录 返回登录
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean check = orderService.checkPath(user,goodsId,path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //判断是否重复抢购
        //直接从redis获取
        SeckillOrder secKillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (secKillOrder != null){
            //被抢购过了
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        if (!EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        Long decrement = (Long) redisTemplate.execute(redisScript, Collections.singletonList("secKillGoods:"+goodsId),Collections.EMPTY_LIST);
        if (decrement <= 0){
            EmptyStockMap.put(goodsId,false);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //处理订单用RabbitMQ
        SecKillMessage secKillMessage = new SecKillMessage(user, goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(secKillMessage));

        return RespBean.success(0);

    }

    @RequestMapping(value = "/{path}/doSecKill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //优化后代码
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //判断是否重复抢购
        SeckillOrder tSeckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (tSeckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记，减少Redis的访问
        if (!EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("secKillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock < 0) {
            EmptyStockMap.put(goodsId, false);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SecKillMessage seckillMessag = new SecKillMessage(user, goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(seckillMessag));
        return RespBean.success(0);

    }

    //获取秒杀结果 有OrderId成功 -1失败 0排队中
    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if (null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    @GetMapping("/path")
    @ResponseBody
    public RespBean getPath(User user,Long goodsId,String captcha){
        if (null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if (!check) return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    @GetMapping("/captcha")
    public void captchaCode(User user, Long goodsId, HttpServletResponse response){
        if (user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
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
