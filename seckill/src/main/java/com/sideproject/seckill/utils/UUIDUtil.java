package com.sideproject.seckill.utils;

import java.util.UUID;

/**
 * UUID工具类
 *
 */
public class UUIDUtil {
    public static final String CookieName = "userTicket";
    public static String genUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

