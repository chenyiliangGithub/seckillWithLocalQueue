package com.sideproject.seckill.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SeckillRollBackMsg {
    Long orderId;
    Integer count;
}
