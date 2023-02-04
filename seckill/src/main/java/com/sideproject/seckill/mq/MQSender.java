package com.sideproject.seckill.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSeckillRollBackMsg(String message) {
        log.info("发送消息：" + message);
        rabbitTemplate.convertAndSend("seckillRollBackMsg", "seckill.msg", message);
    }
}
