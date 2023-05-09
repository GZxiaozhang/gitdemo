package com.example.myseckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myseckill_demo.pojo.SeckillOrder;
import com.example.myseckill_demo.pojo.User;

/**
 * 秒杀订单表 服务类
 *
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     **/
    Long getResult(User tUser, Long goodsId);

}
