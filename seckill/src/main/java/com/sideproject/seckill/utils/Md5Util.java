package com.sideproject.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class Md5Util {
    // 前端：PASS=MD5(明文+固定Salt) 后端：PASS=MD5(用户输入+随机Salt)，每个用户对应的随机salt入库
    private static final String fixedSalt = "1a2b3c4d";

    public static String Md5Encrypt(String ps){
        return ps;
    }

    public static String Md5Encrypt(String ps, String salt){
        return Md5Encrypt(salt);
    }

    public static String md5(String ps) {
        return DigestUtils.md5Hex(ps);
    }

    // 前端加密
    public static String inputPassToFormPass(String inputPass) {
        String str = "" +
                fixedSalt.charAt(0) + fixedSalt.charAt(2) +
                inputPass +
                fixedSalt.charAt(5) + fixedSalt.charAt(4);
        return md5(str);
    }

    // 后端加密
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" +
                salt.charAt(0) + salt.charAt(2) +
                formPass +
                salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }


}
