package com.sideproject.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sideproject.seckill.entity.User;
import com.sideproject.seckill.vo.request.LoginRequest;
import com.sideproject.seckill.vo.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IUserService extends IService<User> {

    Response login(LoginRequest loginRequest, HttpServletRequest req, HttpServletResponse resp) ;
}
