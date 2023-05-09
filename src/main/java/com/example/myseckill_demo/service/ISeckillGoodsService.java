package com.example.myseckill_demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myseckill_demo.pojo.SeckillGoods;
import com.example.myseckill_demo.pojo.SeckillOrder;

/**
 * 秒杀商品表 服务类
 *
 */
public interface ISeckillGoodsService extends IService<SeckillGoods> {

    SeckillOrder getOne(QueryWrapper<SeckillOrder> eq);
}
