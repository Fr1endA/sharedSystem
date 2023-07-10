package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.AlphaHibernates;
import com.nowcoder.community.dao.mapper.DiscussPostMapper;
import com.nowcoder.community.dao.mapper.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.until.CommunityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Date;

@Service
@Scope("singleton")
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Autowired(required = false)
    private TransactionTemplate transactionTemplate;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    @Autowired
    @Qualifier("hibernates")
    private AlphaDao alphaHibernates;
    public String find(){
        return alphaHibernates.select();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ ,propagation = Propagation.REQUIRED)
    public Object save1(){

        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道!");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");


        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

        return transactionTemplate.execute(
                //回调函数，底层调用该回调函数
                new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        // 新增用户
                        User user = new User();
                        user.setUsername("alpha");
                        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                        user.setEmail("alpha@qq.com");
                        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                        user.setCreateTime(new Date());
                        userMapper.insertUser(user);

                        // 新增帖子
                        DiscussPost post = new DiscussPost();
                        post.setUserId(user.getId());
                        post.setTitle("Hello");
                        post.setContent("新人报道!");
                        post.setCreateTime(new Date());
                        discussPostMapper.insertDiscussPost(post);

                        Integer.valueOf("abc");


                        return "ok";
                    }
                }
        );


    }



}
