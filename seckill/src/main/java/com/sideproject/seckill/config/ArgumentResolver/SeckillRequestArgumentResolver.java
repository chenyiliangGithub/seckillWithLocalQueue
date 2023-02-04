package com.sideproject.seckill.config.ArgumentResolver;

import com.sideproject.seckill.utils.UUIDUtil;
import com.sideproject.seckill.vo.request.SeckillRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Random;

/**
 * 为 SeckillRequest 生成 orderId
 */
@Component
public class SeckillRequestArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter){
        return parameter.getParameterType() == SeckillRequest.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        SeckillRequest msg = (SeckillRequest) req.getAttribute("SeckillRequest");

        System.out.println("test convert, msg:"+msg);

        msg.setOrderId(new Random().nextLong(10));
        return msg;
    }
}
