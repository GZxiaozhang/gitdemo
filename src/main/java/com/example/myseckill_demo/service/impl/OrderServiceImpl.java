package com.example.myseckill_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myseckill_demo.exception.GlobalException;
import com.example.myseckill_demo.mapper.OrderMapper;
import com.example.myseckill_demo.pojo.Order;
import com.example.myseckill_demo.pojo.SeckillGoods;
import com.example.myseckill_demo.pojo.SeckillOrder;
import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.service.IGoodsService;
import com.example.myseckill_demo.service.IOrderService;
import com.example.myseckill_demo.service.ISeckillGoodsService;
import com.example.myseckill_demo.service.ISeckillOrderService;
import com.example.myseckill_demo.utils.MD5Util;
import com.example.myseckill_demo.utils.UUIDUtil;
import com.example.myseckill_demo.vo.GoodsVo;
import com.example.myseckill_demo.vo.OrderDetailVo;
import com.example.myseckill_demo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 服务实现类
 *
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private  OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 秒杀
     * @param user
     * @param goodsVo
     * @return
     */
    @Override
    @Transactional  //事务注解
    public Order secKill(User user, GoodsVo goodsVo) {
        ValueOperations valueOperations=redisTemplate.opsForValue();
        /**
         * 秒杀商品表减库存
         */
        SeckillGoods  secKillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        secKillGoods.setStockCount(secKillGoods.getStockCount()-1 );
//        seckillGoodsService.updateById(secKillGoods);
//  写法一:
//      boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().set("stock_count",
//                secKillGoods.getStockCount()).eq("id", secKillGoods.getId()).gt("stock_count", 0));
         boolean result =seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql
                ("stock_count"+"="+"stock_count-1").eq("goods_id", goodsVo.getId()).gt("stock_count",0));
        if(secKillGoods.getStockCount()<1){
            //判断是否还有库存
            valueOperations.set("isStockEmpty:"+goodsVo.getId(),"0");
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(secKillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goodsVo.getId(),seckillOrder);
//        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), SeckillOrder);
        return order;




    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 1, TimeUnit.MINUTES);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    /**
     *  验证码校验
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(captcha)) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId==null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail=new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }
}
