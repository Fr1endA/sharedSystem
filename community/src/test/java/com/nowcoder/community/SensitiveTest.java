package com.nowcoder.community;

import com.nowcoder.community.until.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    SensitiveFilter filter;

    @Test
    public void filterTest(){
        String text="吸毒可以，嫖娼不行，我草泥马";

        text=filter.filter(text);
        System.out.println(text);

        String text1="这里可以☆赌☆博☆,可以☆嫖☆娼☆,可以☆吸☆毒☆,可以☆开☆票☆,哈哈哈!";

        text1=filter.filter(text1);
        System.out.println(text1);
        String temp="";
        temp+=text1.substring(1,10);
        System.out.println(filter.filter(temp));
    }
}
