package com.sideproject.seckill.common.exception;

import com.sideproject.seckill.common.enums.RespEnum;
import com.sideproject.seckill.vo.response.Response;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// 异常处理代码不需要侵入业务代码
// 对于会影响业务的异常，需要在业务中明确异常类型并将其抛出。
@RestControllerAdvice // 定义全局异常处理器。该异常处理器会监控所有的 RestController
public class GlobalExceptionHandler {
//    @ExceptionHandler(value = Exception.class) // 监控所有异常类型，捕获到异常时触发此方法
//    public Response GlobalExceptionHandler(Exception e){
//        return Response.Error(ResponseEnum.ERROR); // 无论请求的是哪个接口，捕获到异常时，自动以 handler 的返回值（转成json？）返回给前端，与接口的返回值类型无关
//    }

    @ExceptionHandler(value = BindException.class) // 注意是 org.springframework.validation.BindException，表示 validation 进行参数校验时抛出的异常
    public Response BindExceptionHandler(BindException e){
        Response resp = Response.error(RespEnum.MOBILE_ERROR);
        resp.setMsg("参数校验异常：" + e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return resp;
    }
}
