package com.sideproject.seckill.config;

import com.sideproject.seckill.config.interceptors.SessionInterceptor;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    // 添加自定义参数解析器
//    @Autowired
//    private UserArgumentResolver userArgumentResolver;
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(userArgumentResolver);
//    }

    @Autowired
    SessionInterceptor sessionInterceptor;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer){
        // 为 RestController 修饰的接口添加统一前缀
        configurer.addPathPrefix("/api", clazz -> clazz.isAnnotationPresent(RestController.class));
    }

    // 添加自定义拦截器 sessionInterceptor，设置拦截规则，拦截以 /api/** 格式的请求（不包含登录请求"/api/login"）
    // 不符合规则的统一拦截，从而限制没有登录过的用户对我们某些接口的访问权限
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/api/**").excludePathPatterns("/api/login");
    }
}

