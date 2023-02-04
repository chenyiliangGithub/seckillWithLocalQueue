package com.sideproject.seckill.service;

import com.sideproject.seckill.vo.request.SeckillRequest;
import com.sideproject.seckill.vo.response.Response;

public interface ISeckillService {
    public Response seckill(SeckillRequest seckillRequest);
}
