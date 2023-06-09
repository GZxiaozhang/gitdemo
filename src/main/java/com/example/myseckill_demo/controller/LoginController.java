package com.example.myseckill_demo.controller;


import com.example.myseckill_demo.vo.LoginVo;
import com.example.myseckill_demo.service.IUserService;
import com.example.myseckill_demo.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j

public class LoginController {

@Autowired
private IUserService userService;


    /*
* 跳转登陆页面
* */

    @RequestMapping(value = "/toLogin")
    public  String toLogin(){
        return  "login";
    }


    //登录功能
    @RequestMapping(value = "/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        log.info(loginVo.toString());
        return userService.login(loginVo,request,response);
    }

}
