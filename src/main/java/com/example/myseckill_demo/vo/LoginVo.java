package com.example.myseckill_demo.vo;


import com.example.myseckill_demo.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

//登陆参数
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {

    @NotNull
    @IsMobile
    private  String mobile;

    @NotNull
    @Length(min=32)
    private  String password;
}
