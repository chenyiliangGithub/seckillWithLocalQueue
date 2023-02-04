package com.sideproject.seckill.vo.request;

import com.sideproject.seckill.common.validator.IsMobile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SeckillRequest {
    @NotNull
    @IsMobile()
    private Long mobile; // 默认的账号格式是手机号码

    private Long orderId; // 由后端解析器生成

    @NotNull
    private Long goodsId;

    @Min(1) // 至少买一件
    private Integer count; // 购买数量
}
