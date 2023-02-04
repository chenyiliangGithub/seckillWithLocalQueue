package com.sideproject.seckill.config.concurrent;

import com.sideproject.seckill.entity.SeckillOrder;
import com.sideproject.seckill.mapper.SeckillGoodsMapper;
import com.sideproject.seckill.mapper.SeckillOrderMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractSeckillMsgConsumer {
    static final int SLEEP_TIME = 20; // 消费者线程休眠时间，单位ms

    @Autowired
    public SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    public SeckillOrderMapper seckillOrderMapper;

    public abstract void consume(Long goodsId, @NotNull SeckillMsgPool pool);

    @Transactional(rollbackFor = Exception.class) // 异常自动抛出
    public int batchOperate(@NotNull List<SeckillOrder> seckillOrderList, Long goodsId, int sumCount) {
        if (seckillOrderList.size() == 0){
            return 0;
        }

        // 扣减库存
        // 成功扣减，flag=1，因为一个秒杀商品的记录就一条；扣减失败，flag = 0
        int flag = seckillGoodsMapper.updateSeckillGoodsStock(sumCount, goodsId);
        if(flag == 0){ // 库存不足的时候，update 不会报错，会继续往下执行，直接在这里 return
            return flag;
        }
        // 插入订单
        // 多条 insert 语句将被包裹在这个事务中 一次性 传过去
        seckillOrderList.forEach(seckillOrder -> seckillOrderMapper.insert(seckillOrder));
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    public int singleOperate(@NotNull SeckillOrder seckillOrder, Long goodsId){
        if (seckillOrder == null){ // null 时，updateSeckillGoodsStock 为 1
            return 0;
        }

        int flag = seckillGoodsMapper.updateSeckillGoodsStock(seckillOrder.getCount(), goodsId);
        if(flag == 0){ // 库存不足的时候，update 不会报错，会继续往下执行，直接在这里 return
            return flag;
        }
        seckillOrderMapper.insert(seckillOrder);
        return flag;
    }
}
