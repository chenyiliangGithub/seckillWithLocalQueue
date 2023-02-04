package com.sideproject.seckill.config.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 封装、限制 对共享对象池的操作
 */
public class SeckillMsgPool {
    static final int POOL_CAPICITY = 1000;
    static final int MAX_POOL_ENQUEUE_WAITTIME = 100; // pool 满时，等待入队的时间
    static final TimeUnit WAIT_TIMEUNIT = TimeUnit.MICROSECONDS;

    private BlockingQueue<SeckillMsg> pool = new LinkedBlockingQueue<>(POOL_CAPICITY);

    public boolean offer(SeckillMsg msg) throws InterruptedException{
        return pool.offer(msg, MAX_POOL_ENQUEUE_WAITTIME, WAIT_TIMEUNIT);
    }

    public SeckillMsg poll() {
        return pool.poll();
    }

    public boolean isEmpty(){
        return pool.isEmpty();
    }

    public int size(){
        return pool.size();
    }
}
