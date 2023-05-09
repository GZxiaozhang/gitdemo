package com.example.myseckill_demo.controller;


import com.example.myseckill_demo.config.AccessLimit;
import com.example.myseckill_demo.exception.GlobalException;
import com.example.myseckill_demo.pojo.Order;
import com.example.myseckill_demo.pojo.SeckillMessage;
import com.example.myseckill_demo.pojo.SeckillOrder;
import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.rabbitmq.MQSender;
import com.example.myseckill_demo.service.IGoodsService;
import com.example.myseckill_demo.service.IOrderService;
import com.example.myseckill_demo.service.ISeckillGoodsService;
import com.example.myseckill_demo.service.ISeckillOrderService;
import com.example.myseckill_demo.utils.JsonUtil;
import com.example.myseckill_demo.vo.GoodsVo;
import com.example.myseckill_demo.vo.RespBean;
import com.example.myseckill_demo.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 *
 * 秒杀
 *      优化前的QPS：785
 *      缓存的QPS：1365
 *      优化后的QPS：2454
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
//秒杀活动的controller，跳转到订单页面（应该做第三方支付什么的）
public class SeckKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private  ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> script;


    /**
     * 获取验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码,将结果直接放在redis中
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }





    //通过内存标记，减少Redis的访问
    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();
    /**
     * 秒杀操作
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    public  String doSecKill2(Model model, User user, Long goodsId){
        if(user==null){
            return "login";
        }
        model.addAttribute("user",user);
        GoodsVo goods  = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存有没有，没有则报错500500
        if(goods.getStockCount() < 1  ){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
//        SeckillOrder seckillOrder = seckillGoodsService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
//                user.getId()).eq("goods_id", goodsId));

        SeckillOrder seckillOrder =(SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return  "secKillFail";
        }
        Order order = orderService.secKill(user, goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";


    }


    /**
     * 秒杀（静态化页面的操作）
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations =redisTemplate.opsForValue();
        boolean check =orderService.checkPath(user,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder =(SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //通过内存标记，减少Redis的访问
        if(EmptyStockMap.get(goodsId)){
            return  RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //redis中的递减库存
//        Long stock = valueOperations.decrement("seckillGoods" + goodsId);
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" +
                goodsId), Collections.EMPTY_LIST);
        if(stock<0){
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return  RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //下单操作
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);

//
//        GoodsVo goods  = goodsService.findGoodsVoByGoodsId(goodsId);
//        //判断库存有没有，没有则报错500500
//        if(goods.getStockCount() < 1  ){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//        //判断是否重复抢购
//        /**
//         * 用redis优化
//         * 优化的思路：减少数据库的访问，在库存判断这块有优化空间，redis预减库存，在redis里进行不够的返回回滚操作
//         * 异步操作，最后一步是客户端做轮询操作，判断是否真的秒杀成功
//         */
//        SeckillOrder seckillOrder = seckillGoodsService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
//                user.getId()).eq("goods_id", goodsId));
//        if(seckillOrder!=null){
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
//        Order order = orderService.secKill(user, goods);
//        return RespBean.success(order);


    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId： 成功   ;-1 秒杀失败 ;0：排队中
     */
        @RequestMapping(value = "/result",method = RequestMethod.GET)
        @ResponseBody
        public  RespBean getResult(User user ,Long goodsId){
        if(user == null){
            return  RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
            Long orderId = seckillOrderService.getResult(user,goodsId);
            return RespBean.success(orderId);
        }

    /**
     * 系统初始化，吧商品库存数量加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            //通过内存标记，减少Redis的访问
            EmptyStockMap.put(goodsVo.getId(),false);
                });
    }

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @param captcha
     * @return com.example.myseckilldemo.vo.RespBean
     **/
    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        // 限制访问次数，5秒内访问5次（通过计数器实现接口的限流）
//        String uri = request.getRequestURI();
//        captcha = "0";
//        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
//        if (count == null) {
//            valueOperations.set(uri + ":" + user.getId(), 1, 5, TimeUnit.SECONDS);
//        } else if (count < 5) {
//            valueOperations.increment(uri + ":" + user.getId());
//        } else {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }





}
