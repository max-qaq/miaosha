package com.edu.maxqaq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author maxqaq
 * @since 2022-04-05
 */
@Getter
@Setter
@TableName("t_seckill_goods")
@ApiModel(value = "SeckillGoods对象", description = "")
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("秒杀商品id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("商品id")
    @TableField("goods_id")
    private Long goodsId;

    @ApiModelProperty("秒杀价格")
    @TableField("seckill_price")
    private BigDecimal seckillPrice;

    @ApiModelProperty("库存数量")
    @TableField("stock_count")
    private Integer stockCount;

    @ApiModelProperty("开始时间")
    @TableField("start_date")
    private LocalDateTime startDate;

    @ApiModelProperty("秒杀结束时间")
    @TableField("end_date")
    private LocalDateTime endDate;


}
