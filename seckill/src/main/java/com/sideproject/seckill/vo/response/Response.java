package com.sideproject.seckill.vo.response;

import com.sideproject.seckill.common.enums.RespEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Response {
    private Integer code;
    private String msg;
    private Object data;

    public static Response success(){
        return new Response(RespEnum.SUCCESS.getCode(), RespEnum.SUCCESS.getMsg(), null);
    }

    public static Response success(Object data){
        return new Response(RespEnum.SUCCESS.getCode(), RespEnum.SUCCESS.getMsg(), data);
    }

    public static Response error(){
        return new Response(RespEnum.ERROR.getCode(), RespEnum.ERROR.getMsg(), null);
    }

    public static Response error(RespEnum respEnum){
        return new Response(respEnum.getCode(), respEnum.getMsg(), null);
    }
}
