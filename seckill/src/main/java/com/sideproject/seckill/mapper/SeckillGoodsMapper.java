package com.sideproject.seckill.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sideproject.seckill.entity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {
    int updateSeckillGoodsStock(int count, Long goodsId);
}
