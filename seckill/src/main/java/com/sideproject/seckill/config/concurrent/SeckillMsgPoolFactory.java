package com.sideproject.seckill.config.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class SeckillMsgPoolFactory {

    @Autowired
    private SeckillMsgConsumer consumer;

    @Autowired
    private SeckillMsgConsumerUnique consumerUnique;

    private ConcurrentMap<Long, SeckillMsgPool> factory = new ConcurrentHashMap<>();

    public SeckillMsgPool getSeckillMsgPool(Long goodsId){
        if(!factory.containsKey(goodsId)){
            synchronized (SeckillMsgPoolFactory.class){ // 如果只通过 bean 单例执行，那么可以用 this 加锁
                if(!factory.containsKey(goodsId)){
                    factory.put(goodsId, new SeckillMsgPool());
                    log.info("为商品："+goodsId+" 创建合并队列并开启消费者线程");
                    new Thread(() -> {
                        consumerUnique.consume(goodsId, factory.get(goodsId));
                    }).start();
                }
            }
        }
        return factory.get(goodsId);
    }
}