package com.example.myseckill_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myseckill_demo.mapper.SeckillGoodsMapper;
import com.example.myseckill_demo.pojo.SeckillGoods;
import com.example.myseckill_demo.pojo.SeckillOrder;
import com.example.myseckill_demo.service.ISeckillGoodsService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * 秒杀商品表 服务实现类
 *
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

    @Override
    public boolean saveBatch(Collection<SeckillGoods> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<SeckillGoods> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<SeckillGoods> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(SeckillGoods entity) {
        return false;
    }

    @Override
    public SeckillGoods getOne(Wrapper<SeckillGoods> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<SeckillGoods> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<SeckillGoods> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public SeckillOrder getOne(QueryWrapper<SeckillOrder> eq) {
        return null;
    }
}
