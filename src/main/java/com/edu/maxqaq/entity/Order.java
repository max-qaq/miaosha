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
@TableName("t_order")
@ApiModel(value = "Order对象", description = "")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("商品id")
    @TableField("goods_id")
    private Long goodsId;

    @ApiModelProperty("收货地址id")
    @TableField("delivery_addr_id")
    private Long deliveryAddrId;

    @ApiModelProperty("冗余的商品名称")
    @TableField("goods_name")
    private String goodsName;

    @ApiModelProperty("商品数量")
    @TableField("goods_count")
    private Integer goodsCount;

    @ApiModelProperty("商品价格")
    @TableField("goods_price")
    private BigDecimal goodsPrice;

    @ApiModelProperty("1pc 2安卓 3ios")
    @TableField("order_channel")
    private Integer orderChannel;

    @ApiModelProperty("订单状态,0新建未支付,1已支付,2已发货,3已收货,4已退款,5已完成")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("订单创建时间")
    @TableField("create_date")
    private LocalDateTime createDate;

    @ApiModelProperty("支付时间")
    @TableField("pay_date")
    private LocalDateTime payDate;


}
