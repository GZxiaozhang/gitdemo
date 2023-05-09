package com.example.myseckill_demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myseckill_demo.exception.GlobalException;
import com.example.myseckill_demo.mapper.UserMapper;
import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.service.IUserService;
import com.example.myseckill_demo.utils.CookieUtil;
import com.example.myseckill_demo.utils.JsonUtil;
import com.example.myseckill_demo.utils.MD5Util;
import com.example.myseckill_demo.utils.UUIDUtil;
import com.example.myseckill_demo.vo.LoginVo;
import com.example.myseckill_demo.vo.RespBean;
import com.example.myseckill_demo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用户表 服务实现类
 */
@Service

public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        //参数校验
//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password))
//        {
//           return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if(null==user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
           throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
//     返回的用户信息放在session里面   request.getSession().setAttribute(ticket,user);
        //将用户信息存在redis中
        redisTemplate.opsForValue().set("user:"+ticket,JsonUtil.object2JsonStr(user));
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)){
            return  null;
        }
        String userJson=(String)redisTemplate.opsForValue().get("user:"+userTicket);
//        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        User user = JsonUtil.jsonStr2Object(userJson,User.class);
        if(user!=null){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return null;

    }

    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user =getUserByCookie(userTicket,request,response);
        if(user==null){
            throw  new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password,user.getSalt()));
        int result = userMapper.updateById(user);
        //删除Redis
        if(1==result){
            redisTemplate.delete("user"+userTicket);
            return  RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
        }



        return null;
    }


}
