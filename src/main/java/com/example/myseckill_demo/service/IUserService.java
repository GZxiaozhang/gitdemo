package com.example.myseckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.vo.LoginVo;
import com.example.myseckill_demo.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 */

public interface IUserService extends IService<User> {



    /**
     * 登录方法
     **/

    RespBean login(LoginVo loginVo,HttpServletRequest request,HttpServletResponse response);

    /**
     * 根据cookie获取用户
     *
     **/
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
    /**
     * 更新密码
     *
     **/
    RespBean updatePassword( String userTicket, String password, HttpServletRequest request, HttpServletResponse response);


}
