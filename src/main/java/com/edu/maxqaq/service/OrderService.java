package com.edu.maxqaq.service;

import com.edu.maxqaq.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author maxqaq
 * @since 2022-04-05
 */
public interface OrderService extends IService<Order> {

    //秒杀操作
    Order secKill(User user, GoodsVo goods);
}
