package com.edu.maxqaq.service;

import com.edu.maxqaq.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.maxqaq.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author maxqaq
 * @since 2022-04-05
 */
public interface GoodsService extends IService<Goods> {

    //获取商品列表
    List<GoodsVo> findGoodsVo();
}
