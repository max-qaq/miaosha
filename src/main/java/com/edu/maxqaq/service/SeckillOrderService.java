package com.edu.maxqaq.service;

import com.edu.maxqaq.entity.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.maxqaq.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author maxqaq
 * @since 2022-04-05
 */
public interface SeckillOrderService extends IService<SeckillOrder> {
//获取秒杀结果
    Long getResult(User user, Long goodsId);
}
