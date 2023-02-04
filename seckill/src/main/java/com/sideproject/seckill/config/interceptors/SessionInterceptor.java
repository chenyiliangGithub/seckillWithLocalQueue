package com.sideproject.seckill.config.interceptors;

import com.sideproject.seckill.utils.CookieUtil;
import com.sideproject.seckill.utils.UUIDUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

// 请求拦截器
@Slf4j
@Component
public class SessionInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object Handler){
        String ticket = CookieUtil.getCookieValue(req, UUIDUtil.CookieName);
        if (StringUtils.isEmpty(ticket)) {
            return true;
        }
        return false;
    }
}


