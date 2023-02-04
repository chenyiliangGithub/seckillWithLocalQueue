package com.sideproject.seckill.config.ArgumentResolver;


import com.sideproject.seckill.utils.CookieUtil;
import com.sideproject.seckill.utils.UUIDUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * ArgumentResolver 与 Interceptor 区别：
 * ArgumentResolver 用于将传来的http请求进行预处理
 * Interceptor 用于拦截请求
 * 举例来说，在二者的代码里都可以判断请求是否合法，但是 ArgumentResolver 只能将非法请求转换为某种数据格式，能够进入对应的接口方法中
 * Interceptor 则能够直接拦截请求，使其无法继续进入对应的接口方法中
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
        // 基于参数类型判断
        // Class<?> clazz = parameter.getParameterType();
        // return clazz == User.class; // 参数属于 User.class 就进行解析

        // 基于注释判断
        // 当 ”参数上“ 有 @EnableSessionCheck 时才使用该解析器解析（执行 resolveArgument 对参数赋值）
        //return parameter.hasParameterAnnotation(EnableSessionCheck.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        
        String ticket = CookieUtil.getCookieValue(req, UUIDUtil.CookieName);
        if (StringUtils.isEmpty(ticket)) {
            return null;
        }
        return req.getSession().getAttribute(ticket);
    }
}


