package com.example.myseckill_demo.controller;

import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.service.IOrderService;
import com.example.myseckill_demo.vo.OrderDetailVo;
import com.example.myseckill_demo.vo.RespBean;
import com.example.myseckill_demo.vo.RespBeanEnum;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 前端控制器
 *
 */
@Controller
@RequestMapping("/order")
@Api(value = "订单", tags = "订单")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    /**
     *  订单详情(静态化)
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user ==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo  detail =orderService.detail(orderId);
        return RespBean.success(detail);
    }


}
