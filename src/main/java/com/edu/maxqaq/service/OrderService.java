package com.edu.maxqaq.service;

import com.edu.maxqaq.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.vo.GoodsVo;
import com.edu.maxqaq.vo.OrderDetailVo;

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

    //订单详情
    OrderDetailVo detail(Long orderId);

    //获取秒杀地址
    String createPath(User user, Long goodsId);

    //校验秒杀地址
    Boolean checkPath(User user, Long goodsId, String path);

    //验证码校验
    Boolean checkCaptcha(User user, Long goodsId, String captcha);
}
