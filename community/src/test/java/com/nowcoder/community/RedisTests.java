package com.nowcoder.community;

import com.nowcoder.community.config.RedisConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey="test:count";

        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println( redisTemplate.opsForValue().increment(redisKey));
        System.out.println( redisTemplate.opsForValue().decrement(redisKey));



    }
    @Test
    public void testHash(){
        String redisKey="test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        redisTemplate.opsForHash().put(redisKey,"password","sad2e1213");
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));


    }

    @Test
    public void testList(){
        String redisKey="test:ids";

        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));

    }

    @Test
    public void testSet(){
        String redisKey="test:teachers";

        redisTemplate.opsForSet().add(redisKey,"刘备","关羽","刘备","张飞");

        redisTemplate.opsForSet().size(redisKey);
        //随机弹出一个值
        redisTemplate.opsForSet().pop(redisKey);
        //统计成员
        redisTemplate.opsForSet().members(redisKey);

    }

    @Test
    public void testSortedSet(){
        String redisKey="test:students";

        redisTemplate.opsForZSet().add(redisKey,"唐僧",90);
        redisTemplate.opsForZSet().add(redisKey,"悟空",110);
        redisTemplate.opsForZSet().add(redisKey,"八戒",60);
        redisTemplate.opsForZSet().add(redisKey,"沙僧",95);
        redisTemplate.opsForZSet().add(redisKey,"白龙马",100);
        redisTemplate.opsForZSet().add(redisKey,"唐僧",100);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"唐僧"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,2));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2));

    }

    // 多次访问同一个key
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }

    // 编程式事务
//    redis 的事务完成前，先将每一条指令存入一个队列中，当执行时，按顺序一并执行。
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
