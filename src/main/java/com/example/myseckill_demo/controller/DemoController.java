package com.example.myseckill_demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 *页面测试
 */

@Controller
@RequestMapping("/demo")
public class DemoController {

    //测试页面跳转
    @RequestMapping("/hello")
    public  String hello(Model model){
    model.addAttribute("name","zhang");
    return "hello";
    }


}
