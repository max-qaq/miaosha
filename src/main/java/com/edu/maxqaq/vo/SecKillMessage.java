package com.edu.maxqaq.vo;

import com.edu.maxqaq.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: miaosha
 * @description: 秒杀信息类
 * @author: max-qaq
 * @create: 2022-04-10 09:14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecKillMessage {

    private User user;

    private Long goodsId;
}
