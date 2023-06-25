package com.nowcoder.community;


import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.dao.mapper.DiscussPostMapper;
import com.nowcoder.community.dao.mapper.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.until.EmailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
public class MailTests {

    @Autowired
    private EmailClient emailClient;

    @Autowired(required = false)
    private TemplateEngine engine=new TemplateEngine();
    @Test
    public void testEmail(){
        emailClient.sendMail("fr1enda@163.com","TestEmail!","Welcome！");
    }

    @Test
    public void testHtmlEmail(){
        Context context=new Context();
        context.setVariable("username","陈奕人");

        String content=engine.process("/mail/demo",context);
        System.out.println(content);

        emailClient.sendMail("fr1enda@163.com","htmlEmail!",content);
    }
}
