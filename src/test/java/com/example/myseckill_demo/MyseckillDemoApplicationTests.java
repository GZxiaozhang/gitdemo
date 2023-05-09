package com.example.myseckill_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

@SpringBootTest
public  class MyseckillDemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Boolean> redisScript;

//    @Test
//    public  void testLock01() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //占位,如果key不存在，才可以设置成功
//        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
//        // 如果占位成功，进行正常操作
//        if(isLock){
//            valueOperations.set("name","xxxx");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name="+name);
//            Integer.parseInt("xxxxx");
//            //操作结束，删除锁
//            redisTemplate.delete("k1");
//        }else {
//            System.out.println("有线程在使用，请稍后再试");
//        }
//    }
//    @Test
//    public void testLock2() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //给锁添加一个过期时间，防止应用在运行过程中抛出异常导致锁无法正常释放。
//        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
//        if (isLock) {
//            valueOperations.set("name", "xxx");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name=" + name);
//            //操作结束，删除锁
//            redisTemplate.delete("k1");
//        } else {
//            System.out.println("有线程在使用，请稍后再试");
//        }
//    }
//
//    @Test
//    public void testLock3() {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String value = UUID.randomUUID().toString();
//        Boolean isLock = valueOperations.setIfAbsent("k1", value, 5, TimeUnit.SECONDS);
//        //使用lua脚本，让多个命令一次执行，lua脚本优点：lua脚本的操作是原子性的，可以去减少网络的传输
//        if (isLock) {
//            valueOperations.set("name", "xxx");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name=" + name);
//            //操作结束，删除锁
//            System.out.println(valueOperations.get("k1"));
//            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
//            System.out.println(result);
//        } else {
//            System.out.println("有线程在使用，请稍后再试");
//        }
//    }

}
