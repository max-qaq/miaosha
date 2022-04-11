package com.edu.maxqaq.service.Impl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.edu.maxqaq.Exception.GlobalException;
import com.edu.maxqaq.entity.Order;
import com.edu.maxqaq.entity.SeckillGoods;
import com.edu.maxqaq.entity.SeckillOrder;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.mapper.OrderMapper;
import com.edu.maxqaq.mapper.SeckillOrderMapper;
import com.edu.maxqaq.service.GoodsService;
import com.edu.maxqaq.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.maxqaq.service.SeckillGoodsService;
import com.edu.maxqaq.service.SeckillOrderService;
import com.edu.maxqaq.utils.MD5Util;
import com.edu.maxqaq.utils.UUIDUtil;
import com.edu.maxqaq.vo.GoodsVo;
import com.edu.maxqaq.vo.OrderDetailVo;
import com.edu.maxqaq.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author maxqaq
 * @since 2022-04-05
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        SeckillGoods secKillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getGoodsId()));
        secKillGoods.setStockCount(secKillGoods.getStockCount() - 1);
        boolean updateResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").eq(
                "goods_id", goods.getGoodsId()).gt("stock_count", 0)
        );
        if (secKillGoods.getStockCount() < 1){
            //秒杀的时候没库存就设置0
            valueOperations.set("isStockEmpty:"+goods.getGoodsId(),0);
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getGoodsId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(secKillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getGoodsId());
        seckillOrderService.save(seckillOrder);
        //订单存到redis
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getGoodsId(),seckillOrder);

        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (null == orderId){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setOrder(order);
        detailVo.setGoodsVo(goodsVo);
        return detailVo;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        //秒杀地址存redis并且设置过期时间
        redisTemplate.opsForValue().set("secKillPath:"+user.getId()+goodsId,str,60, TimeUnit.MINUTES);

        return str;
    }

    @Override
    public Boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("secKillPath:" + user.getId() + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public Boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || StringUtils.isEmpty(captcha) || goodsId < 0) return false;
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
