package com.sideproject.seckill.vo.request;

import com.sideproject.seckill.common.validator.IsMobile;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest { // 前端封装 mobile、password，将其作为 LoginRequest 传入
    // 在用到 LoginRequest 的地方用 @Valid，这样 @NotNull、@IsMobile、@Length才能生效
    @NotNull
    @IsMobile() // 基于注解实现校验参数的功能
    private Long mobile; // 默认的账号格式是手机号码

    @NotNull
    @Length(min = 3)
    private String password;
}
