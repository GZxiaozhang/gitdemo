package com.example.myseckill_demo.utils;

import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;


@Component
public class MD5Util {
    public  static  String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //密钥
    private   static final String salt="1a2b3c4d";

    //第一次加密
    public static  String inputPassToFromPass(String inputPass){
       String str="" +  salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
       return  md5(str);
    }

    //第二次加密 后端到数据库的密码
    public  static String formPassToDBPass(String formPass,String salt){
        String str="" +  salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    //真正需要的，调用的方法
    public static String inputPassToDBPass(String inputPass,String salt){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = formPassToDBPass(fromPass,salt);
        return dbPass;
    }

    public static void main(String[] args) {
        //d3b1294a61a07da9b49b6e22b2cbd7f9  ---第一次加密密码
        System.out.println(inputPassToFromPass("123456"));
        //b27bd94e3ff7aa5c218d8b48183e47f7  ---第二次加密密码
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        //最后要调用的密码
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }


}
