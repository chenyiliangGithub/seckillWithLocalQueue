package com.sideproject.seckill.service.impl;

import com.sideproject.seckill.common.enums.RespEnum;
import com.sideproject.seckill.config.concurrent.SeckillMsg;
import com.sideproject.seckill.config.concurrent.SeckillMsgPoolFactory;
import com.sideproject.seckill.mq.MQSender;
import com.sideproject.seckill.service.ISeckillService;
import com.sideproject.seckill.vo.request.SeckillRequest;
import com.sideproject.seckill.vo.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SeckillServiceImpl implements ISeckillService {

    static final int MAX_WAITING_TIME = 1000; // 用户线程阻塞等待时间，单位ms

    @Autowired
    private SeckillMsgPoolFactory poolFactory;

    @Autowired
    private MQSender mqSender;

    @Override
    public Response seckill(SeckillRequest seckillRequest) {
        SeckillMsg msg = new SeckillMsg(seckillRequest);
        synchronized (msg) {
            // 涉及到对共享变量的操作要注意是否放到同步块中，放到哪个同步块中。以控制并发顺序
            // 这一段入队操作也要放到同步块中
            // 如果放到同步块外卖，有可能：入队 -> 被消费（移除全局队列 + notify） -> wait
            // 这样一来 wait 之后由于已经被消费，信息已经从全局队列中移除，不可能再被消费，也不可能被 notify，只能等 wait 自己超时唤醒
            try {
                if(!poolFactory.getSeckillMsgPool(seckillRequest.getGoodsId()).offer(msg)){
                    log.error("客户端请求被拒绝："+ seckillRequest);
                    return Response.error();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                msg.wait(MAX_WAITING_TIME);
                // 超时了，消费者没有处理 msg 所含的 req
                // 如果是被正常处理，但没有抢到商品，Response 不是 null
                if(msg.getResponse() == null){
                    msg.setStatus(SeckillMsg.USER_TIMEOUT_STATUS); // 消费者处理的时候看到 status=1 就不处理

                    // 发送回滚消息
//                    mqSender.sendSeckillRollBackMsg(JsonUtil.toJson(
//                            new SeckillRollBackMsg(seckillRequest.getOrderId(), seckillRequest.getCount())
//                    ));

                    return Response.error(RespEnum.TIMEOUT_ERROR);
                }
            } catch (InterruptedException e) { // 线程被中断
                return Response.error();
            }
        }

        return msg.getResponse();
    }
}
