package com.sideproject.seckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@TableName("t_seckill_order")
@ApiModel(value = "SeckillOrder对象", description = "")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("秒杀订单id，作为主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单id，用于关联订单表")
    private Long orderId;

    private Long userId;

    private Long goodsId;

    @ApiModelProperty("购买商品数量")
    private Integer count;

    @ApiModelProperty("订单状态，0 表示正常扣减，1 表示订单已回滚，2 表示订单由用户自己取消")
    private Integer status;

}
