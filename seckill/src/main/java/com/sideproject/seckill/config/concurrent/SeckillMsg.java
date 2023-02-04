package com.sideproject.seckill.config.concurrent;

import com.sideproject.seckill.vo.request.SeckillRequest;
import com.sideproject.seckill.vo.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *  为了便于通过 共享变量 让生产者与消费者之前共享信息，新建一个类，包含请求（生产者生成）和响应（消费者生成），作为池的元素类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMsg {
    public static final int USER_TIMEOUT_STATUS = 1;

    private SeckillRequest request;
    private Response response;
    private int status; // 默认值 0，USER_TIMEOUT_STATUS 表示用户等待超时。用 Integer 的话默认为 null

    public SeckillMsg(SeckillRequest request){
        this.request = request;
    }
}
