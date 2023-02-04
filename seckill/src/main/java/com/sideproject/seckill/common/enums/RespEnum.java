package com.sideproject.seckill.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum RespEnum {
    SUCCESS(200, "秒杀成功"),

    ERROR(500, "服务器内部错误"),
    TIMEOUT_ERROR(501, "请求超时"),

    /**
     * 校验错误
     */
    LOGINVO_ERROR(502,"用户名或者密码错误"), // 前端传来的用于登录的 loginvo 有错误
    MOBILE_ERROR(503,"手机号码格式错误"),
    SESSION_ERROR(504,"session不存在或者已经失效"),

    /**
     * 不符合秒杀条件
     */
    EMPTY_STOCK_ERROR(505, "商品库存不足"),
    SECKILLORDER_REPEATE_ERROR(506, "每人限购一件"),
    ORDER_NOT_EXIST_ERROR(507, "订单不存在");

    private final Integer code;
    private final String msg;
}
