package com.example.myseckill_demo.config;

import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类-Headers配置类
 *
 */
@Configuration
public class RabbitMQHeadersConfig {

//    private static final String QUEUE01 = "queue_header01";
//    private static final String QUEUE02 = "queue_header02";
//    private static final String EXCHANGE = "headersExchange";
//
//    @Bean
//    public Queue queue01() {
//        return new Queue(QUEUE01);
//    }
//
//    @Bean
//    public Queue queue02() {
//        return new Queue(QUEUE02);
//    }
//
//    @Bean
//    public HeadersExchange headersExchange() {
//        return new HeadersExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding01() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("color", "red");
//        map.put("speed", "low");
//        return BindingBuilder.bind(queue01()).to(headersExchange()).whereAny(map).match(); //匹配的时候，头部信息headler里有任意一个键值对就可以匹配成功了
//    }
//
//    @Bean
//    public Binding binding02() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("color", "red");
//        map.put("speed", "fast");
//        return BindingBuilder.bind(queue02()).to(headersExchange()).whereAll(map).match();//匹配的时候，头部信息headler里必须全部满足才可以匹配成功
//    }
}
