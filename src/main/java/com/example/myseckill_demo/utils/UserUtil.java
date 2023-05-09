package com.example.myseckill_demo.utils;

import com.example.myseckill_demo.pojo.User;
import com.example.myseckill_demo.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成用户的工具类
 * 抗压测试的时候用，可以看吞吐量等
 */
       // 生成多少个用户
public class UserUtil {
    private  static  void createUser(int count) throws Exception {
        List<User> users =new ArrayList<>(count);
        for(int i=0;i<count;i++){
            User user =new User();
            user.setId(1770000000L+i);
            user.setNickname("user"+i);
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setSalt("1a2b3c4d");
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");
        /**
         * 插入数据库的操作
         * jdbc的操作
         * 将上述生成的用户插入到数据库当中,通过批量来执行
         */
        Connection conn=getConn();
        String sql="insert into t_user(login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(int i=1;i<users.size();i++) {
            User user = users.get(i);
            pstmt.setInt(1,user.getLoginCount());
            pstmt.setString(2,user.getNickname());
            pstmt.setTimestamp(3,new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4,user.getSalt());
            pstmt.setString(5,user.getPassword());
            pstmt.setLong(6,user.getId());
            pstmt.addBatch(); // 添加批量

        }
        pstmt.executeBatch();
        pstmt.clearParameters();
        conn.close();
        System.out.println("insert to DB");
        //登录，生成UserTicket
        String urlString="http://localhost:8080/login/toLogin";
        File file =new File("E:\\IDEA\\practise\\myseckill_demo\\config.txt");  //在测压时候用到的UserTicket和userId，放置在config.txt
        //文件存在和不存在对应的操作（包括字节流、文件流的输出输入转换加密等）
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        raf.seek(0);
        for(int i=0;i<users.size();i++){
            User user=users.get(i);
            URL url =new URL(urlString);
            HttpURLConnection co =(HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            OutputStream out =co.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream=co.getInputStream();
            ByteArrayOutputStream bout =new ByteArrayOutputStream();
            byte[] buff= new byte[1024];
            int len=0;
            while ((len=inputStream.read(buff))>0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            //输入流关闭，打开输出流将数据存到respBean中，
            String response=new String(bout.toByteArray());  //拿到响应结果
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket =(String) respBean.getObj();
            System.out.println("create userTicket:" + user.getId());
            String row =user.getId()+","+userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file :" +user.getId());

        }

         raf.close();
        System.out.println("over");

    }


    //获取数据库时的方法 getConn（）

    private static Connection getConn() throws Exception {
        String url ="jdbc:mysql://127.0.0.1:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username="root";
        String password="ZHr123580";
        String driver="com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
        
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
