package com.edu.maxqaq.mapper;

import com.edu.maxqaq.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.maxqaq.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author maxqaq
 * @since 2022-04-05
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
