package com.edu.maxqaq.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: miaosha
 * @description: 商品返回对象
 * @author: max-qaq
 * @create: 2022-04-05 22:07
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo {
    //商品id
    private Long goodsId;
    //商品名称
    private String goodsName;
    //商品标题
    private String goodsTitle;
    //商品图片
    private String goodsImg;
    //商品详情
    private String goodsDetail;
    //商品价格
    private BigDecimal goodsPrice;
    //商品库存
    private Integer goodsStock;
    //秒杀价格
    private BigDecimal seckillPrice;
    //秒杀库存数
    private Integer stockCount;
    //开始时间
    private Date startDate;
    //结束时间
    private Date endDate;

}
