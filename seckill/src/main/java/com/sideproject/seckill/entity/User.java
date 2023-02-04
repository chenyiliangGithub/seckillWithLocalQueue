package com.sideproject.seckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("t_user")
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户账号，暂定格式是手机号码")
    @TableId(value = "mobile", type = IdType.AUTO)
    private Long mobile;
    private String name;

    @ApiModelProperty("MD5(MD5(ps明文+salt))")
    private String password;
    private String salt;

    private Date createTime;
    private Date lastLoginTime;
}
