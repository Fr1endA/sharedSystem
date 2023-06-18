package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.mapper.UserMapper;
import com.nowcoder.community.until.CommunityConstant;
import com.nowcoder.community.until.CommunityUtil;
import com.nowcoder.community.until.EmailClient;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
//service调用dao层接口
//面向接口编程
//调用方法

@Service
public class UserService implements CommunityConstant {


    @Autowired
    private UserMapper userMapper;


    @Autowired
    private EmailClient emailClient;

    @Autowired(required = false)
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    public User findUserById(int id){
        return userMapper.selectById(id);

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
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }



    }
}
