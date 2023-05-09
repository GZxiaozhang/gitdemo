package com.example.myseckill_demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {
//    @Autowired
//    private IUserService UserService;
//    @Autowired
//    private MQSender mqSender;
//
//
//    /**
//     * 用户信息（测试用）
//     * @param user
//     * @return
//     */
//    @RequestMapping(value = "/info", method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("返回用户信息")
//    public RespBean info(User user) {
//        return RespBean.success(user);
//    }
//
//
//    /**
//     * 测试发送RabbitMQ的消息
//     */
//    @RequestMapping(value = "/mq", method = RequestMethod.GET)
//    @ResponseBody
//    public void mq() {
//        mqSender.send("Hello");
//    }
//    /**
//     * Fanout模式（交换机）
//     */
//    @RequestMapping(value = "/mq/fanout", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqFanout() {
//        mqSender.send("Hello");
//    }
//
//    /**
//     * Direct模式
//     */
//    @RequestMapping(value = "/mq/direct01", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqDirect01() {
//        mqSender.send01("Hello Red");
//    }
//
//    @RequestMapping(value = "/mq/direct02", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqDirect02() {
//        mqSender.send02("Hello Green");
//    }
//
//    /**
//     * Topic模式
//     */
//    @RequestMapping(value = "/mq/topic01", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqtopic01() {
//        mqSender.send03("Hello Red");
//    }
//
//    @RequestMapping(value = "/mq/topic02", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqtopic02() {
//        mqSender.send04("Hello Green");
//    }
//
//    /**
//     * Header模式
//     */
//    @RequestMapping(value = "/mq/header01", method = RequestMethod.GET)
//    @ResponseBody
//    public void header01() {
//        mqSender.send05("Hello 01");
//    }
//
//    @RequestMapping(value = "/mq/header02", method = RequestMethod.GET)
//    @ResponseBody
//    public void header02() {
//        mqSender.send06("Hello 02");
//    }


}
