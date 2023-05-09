package com.example.myseckill_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myseckill_demo.pojo.Goods;
import com.example.myseckill_demo.vo.GoodsVo;

import java.util.List;

/**
 * 商品表 Mapper 接口
 *
 */
public interface GoodsMapper extends BaseMapper<Goods> {

        List<GoodsVo> findGoodsVo();

        /**
         * 获取商品详情
         */
        GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
