package com.edu.maxqaq.service.Impl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.maxqaq.entity.Order;
import com.edu.maxqaq.entity.SeckillGoods;
import com.edu.maxqaq.entity.SeckillOrder;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.mapper.OrderMapper;
import com.edu.maxqaq.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.maxqaq.service.SeckillGoodsService;
import com.edu.maxqaq.service.SeckillOrderService;
import com.edu.maxqaq.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Order secKill(User user, GoodsVo goods) {
        SeckillGoods secKillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getGoodsId()));
        secKillGoods.setStockCount(secKillGoods.getStockCount() - 1);
        seckillGoodsService.updateById(secKillGoods);
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

        return order;
    }
}
