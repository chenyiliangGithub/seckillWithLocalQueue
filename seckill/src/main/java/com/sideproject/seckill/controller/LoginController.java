package com.sideproject.seckill.controller;

import com.sideproject.seckill.service.IUserService;
import com.sideproject.seckill.vo.request.LoginRequest;
import com.sideproject.seckill.vo.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private IUserService userService;

    @RequestMapping("/doLogin")
    @ResponseBody

    public Response DoLogin(@Valid LoginRequest loginRequest,  // @Valid 基于注释实现校验功能
                            HttpServletRequest req, HttpServletResponse resp){
        return userService.login(loginRequest, req, resp);
    }
}
