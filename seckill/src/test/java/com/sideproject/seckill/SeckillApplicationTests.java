package com.sideproject.seckill;

import com.sideproject.seckill.config.concurrent.SeckillMsgConsumer;
import com.sideproject.seckill.entity.SeckillOrder;
import com.sideproject.seckill.mapper.SeckillGoodsMapper;
import com.sideproject.seckill.mapper.SeckillOrderMapper;
import com.sideproject.seckill.service.ISeckillService;
import com.sideproject.seckill.vo.request.SeckillRequest;
import com.sideproject.seckill.vo.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private ISeckillService seckillService;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillMsgConsumer seckillMsgConsumer;

    @Test
    public void linkedListTest(){
        List<String> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add(String.valueOf(i));
        }

        for (Iterator<String> iter = list.iterator(); iter.hasNext();){
            String i = iter.next();
            if(i.equals("2")){
                iter.remove();
            }
        }

        for (String i : list){
            System.out.println(i);
        }
    }

    @Test
    public void mapperTest(){
        /**
         * 第一次操作数据库，用时 346 ms
         * 此后相同操作用时只需 1ms 左右
         * 推测是：
         *
         * spring 连接数据库采用的是懒加载方式
         * 第一次操作数据库的时候，需要连接数据库，用时较长。如果没有操作数据库，就不创建连接。
         * 之后复用已经存在的连接，省去了连接用时
         */
        for (int i=0;i<10;++i){
            long s = System.currentTimeMillis();

            seckillGoodsMapper.updateSeckillGoodsStock(2, Long.valueOf(1));
            System.out.println(System.currentTimeMillis() - s);
        }
    }

    @Test
    public void mapperTest1(){
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(1L);
        seckillOrder.setUserId(1L);
        seckillOrder.setGoodsId(1L);
        seckillOrder.setCount(1);
        seckillOrder.setStatus(0);

        List<SeckillOrder> seckillOrderList = new ArrayList<>();
        seckillOrderList.add(seckillOrder);
        try {
            if(seckillMsgConsumer.batchOperate(seckillOrderList, 1L, 1) == 1){
                System.out.println(1);
            }
        } catch (Exception e) {

            System.out.println(2);
        }

    }

    @Test
    public void serviceTest(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Response>> futureList = new ArrayList<>();

        for (int i=0;i<10;++i){
            Long goodsId = new Random().nextLong(1,3);
            int finalI = i;
            Long mobile = new Random().nextLong(1,3);
            Future<Response> future = executorService.submit(() -> { // 任务放到线程池中，执行时间不确定
                //return seckillService.seckill(new SeckillRequest(mobile, (long) finalI, goodsId, 1));
                return seckillService.seckill(new SeckillRequest(1L, 1L, 1L, 1));
            });

            futureList.add(future);
        }

        futureList.forEach(
                future -> {
                    try {
                        // 346 ms以上，因为懒加载连接数据库
                        Response response = future.get(1000, TimeUnit.MILLISECONDS); // 若 future 绑定的任务已执行完，直接拿到，否则阻塞等待
                        System.out.println("客户端请求响应：" + response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Test
    void contextLoads() {
    }

}
