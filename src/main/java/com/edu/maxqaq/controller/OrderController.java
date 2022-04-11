package com.edu.maxqaq.controller;


import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.service.OrderService;
import com.edu.maxqaq.vo.OrderDetailVo;
import com.edu.maxqaq.vo.RespBean;
import com.edu.maxqaq.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author maxqaq
 * @since 2022-04-05
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/detail")
    public RespBean detail(User user,Long orderId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
