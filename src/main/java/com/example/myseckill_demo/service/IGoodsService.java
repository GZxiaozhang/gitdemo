package com.example.myseckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myseckill_demo.pojo.Goods;
import com.example.myseckill_demo.vo.GoodsVo;

import java.util.List;

/**
 * 获取商品列表   服务类
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 返回商品列表
     *
     **/
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     */

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
