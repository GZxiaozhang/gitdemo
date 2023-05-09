package com.example.myseckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myseckill_demo.pojo.Order;
import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.vo.GoodsVo;
import com.example.myseckill_demo.vo.OrderDetailVo;

/**
 * 服务类
 *
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     *
     **/
    Order secKill(User user, GoodsVo goodsVo);



    /**
     * 获取秒杀地址
     *
     **/
    String createPath(User user, Long goodsId);

    /**
     * 校验秒杀地址
     *
     **/
    boolean checkPath(User user, Long goodsId, String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);
}
