package com.edu.maxqaq.vo;

/**
 * @program: miaosha
 * @description: 订单详情
 * @author: max-qaq
 * @create: 2022-04-10 21:11
 **/

import com.edu.maxqaq.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private Order order;

    private GoodsVo goodsVo;
}
