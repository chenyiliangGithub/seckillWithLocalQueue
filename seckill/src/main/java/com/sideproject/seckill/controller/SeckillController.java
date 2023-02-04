package com.sideproject.seckill.controller;

import com.sideproject.seckill.service.ISeckillService;
import com.sideproject.seckill.vo.request.SeckillRequest;
import com.sideproject.seckill.vo.response.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private ISeckillService seckillService;

    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    public Response DoSeckill(@Valid SeckillRequest seckillRequest){
        return seckillService.seckill(seckillRequest);
    }


}
