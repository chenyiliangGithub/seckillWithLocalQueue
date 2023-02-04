package com.sideproject.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sideproject.seckill.common.enums.RespEnum;
import com.sideproject.seckill.entity.User;
import com.sideproject.seckill.mapper.UserMapper;
import com.sideproject.seckill.service.IUserService;
import com.sideproject.seckill.utils.CookieUtil;
import com.sideproject.seckill.utils.Md5Util;
import com.sideproject.seckill.utils.UUIDUtil;
import com.sideproject.seckill.vo.request.LoginRequest;
import com.sideproject.seckill.vo.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Response login(LoginRequest loginRequest, HttpServletRequest req, HttpServletResponse resp) {
        Long mobile = loginRequest.getMobile();
        String formPass = loginRequest.getPassword();

        User user = userMapper.selectById(mobile);
        // 用户的 salt 在创建账号的时候生成
        if(user == null || !Md5Util.formPassToDBPass(formPass, user.getSalt()).equals(user.getPassword())){
            return Response.error(RespEnum.LOGINVO_ERROR);
        }

        /**
         * session 进行权限控制的逻辑
         * 1. 用户访问登录接口，通过登录校验后，为用户绑定一个 ticket，回传前端
         * 2. 后端服务器存储（session存储(ticket, user)），前端浏览器存储（cookie存储ticket）
         * 3. 前端带上 cookie，继续调用后端任意接口
         * 4. 后端配置了 Resolver，满足解析的条件的话，将从 cookie 提取 ticket，并根据 ticket，查询 session 中是否有对应的 user
         * 5. 有则说明之前登录过，可以访问页面。否则，不允许其访问商品界面，必须先登录才行
         *
         * 需要说明的是
         * 1. 接口包含参数 user 的话，表示调用接口需要判断用户是否已经登录
         * 2. 登录接口也可以通过判断 session，避免重复执行（查询数据库、md5加密判断、生成cookie和session）这三个操作
         * 由于登录接口不需要参数 User，因此解析的条件可以通过注释，而非判断参数是否为 User 类型
         *
         * 问题：
         * 1. 如果是微服务架构，且服务有多台实例，不同的实例上 session 会存在不一致现象？
         * 2. 基于 java.util.UUID 生成 ticket，如果是微服务架构，且服务有多台实例，是否能保证不重复？
         */
        String ticket = UUIDUtil.genUUID(); // 生成标识用户的 ticket
        req.getSession().setAttribute(ticket, true); // 用 session 保存用户信息，这里简单存个 true 表示 session 存在
        CookieUtil.setCookie(req, resp, UUIDUtil.CookieName, ticket); // 将 ticket 以 cookie 形式存到 resp 中，回传前端
        return Response.success();
    }
}
