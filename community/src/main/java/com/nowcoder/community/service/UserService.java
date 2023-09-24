package com.nowcoder.community.service;

import com.mysql.cj.log.Log;
import com.nowcoder.community.dao.mapper.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.mapper.UserMapper;
import com.nowcoder.community.until.CommunityConstant;
import com.nowcoder.community.until.CommunityUtil;
import com.nowcoder.community.until.EmailClient;
import com.nowcoder.community.until.RedisKeyUtil;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
//service调用dao层接口
//面向接口编程
//调用方法

@Service
public class UserService implements CommunityConstant {


    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private EmailClient emailClient;

    @Autowired(required = false)
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RedisTemplate redisTemplate;


    public User findUserById(int id){
//        return userMapper.selectById(id);
        User user=getCache(id);
        if(user==null){
            user=initCache(id);
        }
        return user;

    }
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    public Map<String,Object> register(User user){

        Map<String,Object> map=new HashMap<>();

        //空值判断
        if(user==null){
            throw new IllegalArgumentException("参数不为空");
        }
        if(StringUtils.isBlank((user.getUsername()))){
            map.put("usernameMessage","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank((user.getPassword()))){
            map.put("passwordMessage","密码不能为空！");
            return map;
        }

        if(StringUtils.isBlank((user.getEmail()))){
            map.put("emailMessage","邮箱不能为空！");
            return map;
        }

        //验证账号,邮箱是否已存在（调用usermapper）
        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMessage","该账号已被注册！");
            return map;
        }

        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMessage","该邮箱已被注册");
            return map;
        }

        //通过验证， 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
//        http://localhost:8080/community/activiation/#{userid}/code

        String url=domain+contextPath+"/activation/" + user.getId() + "/" +user.getActivationCode();
        context.setVariable("url", url);

        String content=templateEngine.process("/mail/activation",context);
        emailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    public int activation(int userId,String code){


        User user=userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);

            clearCache(userId);

            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }



    }

    public Map<String,Object> login(String username,String password,int expirationSecond){
        Map<String,Object> map=new HashMap<>();

//        空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        User u = userMapper.selectByName(username);
        //验证用户
        if(u==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        //验证状态
        if(u.getStatus()==0){
            map.put("usernameMsg","账号未激活");
            return map;
        }
        //验证密码
        password=CommunityUtil.md5(password+u.getSalt());
        if(!password.equals(u.getPassword())){
            map.put("passwordMsg","密码错误");
            return map;
        }

        //生成登录凭证

        LoginTicket loginTicket =new LoginTicket();
        loginTicket.setUserId(u.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expirationSecond * 1000));

//        loginTicketMapper.insertLoginTicket(loginTicket);

//        redis存ticket
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());

        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;


    }



    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);

    }

    public LoginTicket findLoginTicket(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
//        return /*loginTicketMapper.selectByTicket(ticket)*/;
        return loginTicket;
    }

    public int updateHeader(int userId,String headerUrl){
//        clearCache(userId);
//        return userMapper.updateHeader(userId,headerUrl);
        int rows=userMapper.updateHeader(userId,headerUrl);
        if(rows!=0){
            clearCache(userId);
        }
        return rows;


    }

    public int updatePassword(int userId,String newPassword){
        return userMapper.updatePassword(userId,newPassword);
    }

//    1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey= RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
//    2.若缓存取不到，则初始化缓存数据
    private User initCache(int userId){

        User user=userMapper.selectById(userId);

        String redisKey= RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
//    3.当数据变更时，清除缓存数据
    private void clearCache(int userId){
        String redisKey= RedisKeyUtil.getUserKey(userId);

        redisTemplate.delete(redisKey);

    }

}
