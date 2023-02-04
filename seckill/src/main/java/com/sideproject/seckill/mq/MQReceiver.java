package com.sideproject.seckill.mq;

import com.sideproject.seckill.mapper.SeckillOrderMapper;
import com.sideproject.seckill.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("QUEUE接受消息：" + msg);

        SeckillRollBackMsg message = JsonUtil.toObj(msg, SeckillRollBackMsg.class);


    }

    @Transactional
    public void rollBackSeckillOrder(SeckillRollBackMsg msg){
        seckillOrderMapper.rollBackSeckillOrder(msg.getOrderId(), msg.getCount());
    }
}
