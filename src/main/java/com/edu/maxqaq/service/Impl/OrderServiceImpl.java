package com.edu.maxqaq.service.Impl;

import com.edu.maxqaq.entity.Order;
import com.edu.maxqaq.mapper.OrderMapper;
import com.edu.maxqaq.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
