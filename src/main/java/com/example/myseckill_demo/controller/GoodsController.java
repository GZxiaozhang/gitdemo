package com.example.myseckill_demo.controller;


import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.service.IGoodsService;
import com.example.myseckill_demo.service.IUserService;
import com.example.myseckill_demo.vo.DetailVo;
import com.example.myseckill_demo.vo.GoodsVo;
import com.example.myseckill_demo.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/goods")
//跳转到商品列表页
public class GoodsController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver; //手动渲染的模板引擎


    @RequestMapping(value = "/toList2", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
    @ResponseBody
    public String toList2(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!org.thymeleaf.util.StringUtils.isEmpty(html)) {
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!org.thymeleaf.util.StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**\
     * 跳转商品页
     * windows优化前jmeter的QPS：1332
     *                  缓存QPS：2342
     * linux优化前jmeter的QPS:207 (因为linux的配置比较低)
     * @param model
     * @param user
     * @return
     */
    @RequestMapping( value="/toList",produces = "text/html;charset=utf-8",method = RequestMethod.GET)
    @ResponseBody
    public String toList(Model model, User user, HttpServletResponse response, HttpServletRequest request){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html =(String)valueOperations.get("goodsList");
        /**
         * redis页面缓存------获取的html页面如果不为空，则直接返回html
         */
        if(StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
//        return "goodsList";
        /**
         * 如果为空，则需要手动渲染，存入redis并返回
         * */
        WebContext context = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap()); //获取thymeleafViewResolver模板渲染需要的一些参数
         html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context); //手动渲染,之后在redisDesktop上会有数据记录
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return  html;
    }



    /**
     * 跳转商品详情页
     */
    @RequestMapping(value = "/toDetail2/{goodsId}",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toDetail2(Model model, User user, @PathVariable Long goodsId,
                           HttpServletRequest request,HttpServletResponse response ) {
        /**
         * 对商品详情页进行redis页面缓存的数据处理
         */
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:"+goodsId);
        /**
         * redis中获取页面，如果不为空，直接返回页面
         */
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //剩余开始时间
        int remainSeconds  = 0;
        //秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
            // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
//        model.addAttribute("goods",goodsVo);

        /**
         * redis中获取页面，如果为空，则进行页面渲染
         */
        WebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:"+goodsId,html,60,TimeUnit.SECONDS);
        }
//        return "goodsDetail";
        return  html;

    }



    /**
     * 跳转商品详情页
     *(商品详情页面静态化)
     */

    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId ) {

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods", goods);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //剩余开始时间
        int remainSeconds  = 0;
        //秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
            // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo =new DetailVo();
        detailVo.setGoodsVo(goods);
        detailVo.setUser(user);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);

    }

}

//    @RequestMapping("/toDetail")
//    public String toDetail( Model model,User user){
////        if(StringUtils.isEmpty(ticket)){
////            return "login";
////        }
//////        User user = (User) session.getAttribute(ticket);
////        User user = userService.getUserByCookie(ticket, request, response);
////        if(null==user){
////            return "login";
////        }
//        model.addAttribute("user",user);
//        return "goodsList";
//    }

