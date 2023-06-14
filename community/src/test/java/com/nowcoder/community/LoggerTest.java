package com.nowcoder.community;


import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.dao.mapper.DiscussPostMapper;
import com.nowcoder.community.dao.mapper.UserMapper;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
public class LoggerTest {
    private static Logger logger= LoggerFactory.getLogger(LoggerTest.class);


    @Test
    public void testLogger(){
        System.out.println(logger);

        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

    }

}
