package com.sideproject.seckill.config.concurrent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sideproject.seckill.common.enums.RespEnum;
import com.sideproject.seckill.entity.SeckillOrder;
import com.sideproject.seckill.vo.request.SeckillRequest;
import com.sideproject.seckill.vo.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 用户只能购买一次，订单唯一，唯一索引 order_id+user_id
 */
/**
 * 从全局的用户信息池收集一批用户信息
 * 实现：一个线程（单消费者），定时任务，以死循环的方式从池中拿，池空则休眠一段时间。
 * 若多消费者注意以下情况：
 * 1. 取 pool 元素会竞争
 * 2. 对数据库先读再写，不能保证读的时候数据是什么样写的时候数据还是什么样
 */
@Component
@Slf4j
public class SeckillMsgConsumerUnique extends AbstractSeckillMsgConsumer {

    public void consume(Long goodsId, @NotNull SeckillMsgPool pool) {
        // list，遍历的顺序与添加元素的顺序一致。若为 set，遍历的顺序不能保证与添加元素的顺序一致。这种一致性可以尽量保证按时间顺序公平处理请求
        // 链表，便于移除元素
        List<SeckillMsg> list = new LinkedList<>();
        while (true){
            if(pool.isEmpty()){
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // 有多少拿多少，多个消费者就不能这么写
            for (int i=0;i<pool.size();++i){
                list.add(pool.poll());
            }

            Set<Long> userIdSet = new HashSet<>();
            List<SeckillMsg> operatedList = new ArrayList<>();
            while (list.size() > 0){
                // 上一轮循环中 operatedList 剩余的元素是已经处理了的请求
                for (SeckillMsg seckillMsg : operatedList){
                    if(seckillMsg.getResponse() == null){ // 扣库存失败
                        seckillMsg.setResponse(Response.error());
                    }
                    // 消费完成，唤醒用户
                    synchronized (seckillMsg){
                        seckillMsg.notify();
                    }
                }
                // 上一轮的请求处理完毕，清空，避免重复处理
                operatedList.clear();
                userIdSet.clear();

                // 一个用户的多个请求不能批量处理，因为这两个请求会先后 insert，后 insert 同用户订单的时候一定会失败（失败前提是加了唯一索引 userId_goodsId）
                // 法1：只有一个 msg 的用户批量扣减，有多个 msg 的用户单独扣减。缺点：有多个请求的用户要不先执行，要不后执行。先执行的话对其他用户不公平，后执行的话对这些用户不公平
                // 法2：分层处理。每次从 list 中提取每个用户的一个请求，合并处理，直至 list 为空
                for (Iterator<SeckillMsg> iter = list.iterator(); iter.hasNext();){
                    SeckillMsg msg = iter.next();
                    Long userId = msg.getRequest().getMobile();

                    if(!userIdSet.contains(userId)){
                        userIdSet.add(userId);
                        operatedList.add(msg);

                        iter.remove();
                    }
                }

                /**
                 * 库存扣减方案：
                 * 以事务的方式执行 扣库存+创建订单
                 * 假设用户 A 生成了 seckillMsg，由于消费者消费不及时，A 等待超时，回传前端 ”请求超时“
                 * 但是之后消费者消费了该 seckillMsg，则：
                 * 1. 如果扣库存成功，需要回滚库存（因为已经告诉用户”请求超时“）
                 * 2. 如果扣库存失败，不需要创建订单，自然也不需要回滚库存
                 * 因此，当用户等待超时的时候，要触发回滚逻辑，但是回滚前提是 “用户的订单已经落库”
                 * 为了保证触发回滚逻辑时满足条件，
                 * 法1：本地记录哪些用户的购买订单已经落库，用户等待超时的时候把用户注册到一个表中，由一个异步线程统一管理并触发这些超时用户的回滚操作
                 * 法2：用户等待超时的时候发一条消息到消息队列，由一消息队列统一管理并触发这些超时用户的回滚操作
                 * 建议用 mq，保证消息不丢失就行。本地缓存的话有丢失风险，不能设置过期会越堆越多，并且对这些回滚操作没有持久化机制
                 *
                 * 注意：
                 * 回滚操作要保证幂等性
                 *
                 */

                /**
                 * 乐观考虑，先无锁判断一下，能通过则后面更新（加锁）操作通过的概率更高，
                 * 由于对某个商品的操作只有一个线程在做，没有多线程，因此实际上前面查询到的库存和订单，在后面执行 update 的时候仍旧是成立的
                 * 如果多实例，前面查询到的库存，在后面执行 update 的时候就不一定成立了
                 */
                int stock = seckillGoodsMapper.selectById(goodsId).getStock();
                // 已经没有库存，不需要再更新库存，避免无意义的加锁
                if(stock <= 0){
                    break;
                }
                // 过滤已经创建了订单的用户，并创建订单
                List<SeckillOrder> seckillOrderList = new ArrayList<>();
                int sumCount = 0;
                for (SeckillMsg msg : operatedList){
                    if(seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().
                            eq("user_id", msg.getRequest().getMobile()).
                            eq("goods_id", goodsId)) != null){
                        msg.setResponse(Response.error(RespEnum.SECKILLORDER_REPEATE_ERROR));
                        log.info("user_id="+msg.getRequest().getMobile()+", goods_id="+goodsId+" 的订单已存在");
                        continue;
                    }

                    SeckillRequest request = msg.getRequest();
                    seckillOrderList.add(new SeckillOrder(
                            null,
                            request.getOrderId(), request.getMobile(), request.getGoodsId(), request.getCount(),
                            0
                    ));
                    sumCount += request.getCount();
                }
                if (seckillOrderList.size() == 0){
                    continue;
                }

                // 1. 批量扣减库存
                try {
                    if (stock >= operatedList.size() && batchOperate(seckillOrderList, goodsId, sumCount) == 1) {
                        operatedList.forEach(msg -> msg.setResponse(Response.success()));
                        log.info(Thread.currentThread().getName() + "：合并扣减库存" + operatedList);
                        continue;
                    }
                } catch (Exception e) {
                    log.error("数据库更新异常，seckillOrderList = "+seckillOrderList);
                    e.printStackTrace();
                }
                // 2. 库存不足以批量扣减，遍历用户扣减库存
                // 至多扣 stock 次
                for (int i = 0; i < operatedList.size() && stock > 0; i++) {
                    SeckillMsg msg = operatedList.get(i);
                    if(msg.getStatus() == SeckillMsg.USER_TIMEOUT_STATUS){ // 用户等待超时，不继续操作数据库
                        continue;
                    }

                    try {
                        if(singleOperate(seckillOrderList.get(i), goodsId) == 1){
                            msg.setResponse(Response.success());
                            --stock;
                            log.info(Thread.currentThread().getName() + "：单独扣减库存 " + seckillOrderList.get(i));
                        }
                    } catch (Exception e) {
                        log.error("数据库更新异常，seckillOrder = "+seckillOrderList.get(i));
                        e.printStackTrace();
                    }
                }
            }
            // 处理最后一轮的请求
            for (SeckillMsg seckillMsg : operatedList){
                if(seckillMsg.getResponse() == null){ // 扣库存失败，库存不足，或者是操作数据库的时候出错
                    seckillMsg.setResponse(Response.error());
                }
                // 消费完成，唤醒用户
                synchronized (seckillMsg){
                    seckillMsg.notify();
                }
            }
        }
    }
}
