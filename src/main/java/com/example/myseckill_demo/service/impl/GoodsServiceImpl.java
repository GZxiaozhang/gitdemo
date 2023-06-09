package com.example.myseckill_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myseckill_demo.mapper.GoodsMapper;
import com.example.myseckill_demo.pojo.Goods;
import com.example.myseckill_demo.service.IGoodsService;
import com.example.myseckill_demo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品表 服务实现类
 *
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
    * 获取商品列表
    * */
    @Override
    public List<GoodsVo> findGoodsVo(){
        return goodsMapper.findGoodsVo();
    }

    /**
     * 获取商品详情
     */

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId){
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }

}
