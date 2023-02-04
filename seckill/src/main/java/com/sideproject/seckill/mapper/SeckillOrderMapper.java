package com.sideproject.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sideproject.seckill.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    int rollBackSeckillOrder(Long orderId, Integer count);
}
