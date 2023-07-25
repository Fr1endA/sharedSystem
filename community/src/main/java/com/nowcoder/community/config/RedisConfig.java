package com.nowcoder.community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {



    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

//        java序列化
//        设置： 1.key序列化方式
        template.setKeySerializer(RedisSerializer.string());
//        设置：2.普通value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
//        设置：hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
//        设置：hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();

        return template;

    }
}
