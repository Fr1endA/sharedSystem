package com.nowcoder.community.config;

import com.google.code.kaptcha.NoiseProducer;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
//    第三方库的类加入Spring容器管理，主动声明@Bean 加入管理
    public Producer kaptchaProducer(){
//        Producer本质为一个接口，Default是默认实例
//        实例化接口→bean
        DefaultKaptcha defaultKaptcha=new DefaultKaptcha();
//          Properties是配置信息的管理对象，以KV形式存在
        Properties properties= new Properties();

        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
//
        Config config = new Config(properties);

        defaultKaptcha.setConfig(config);

        return defaultKaptcha;

    }
}
