package com.nowcoder.community.until;




import io.micrometer.common.util.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

//    生成激活码，随机字符串

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");

    }

//    MD5加密
//    只能加密不能解密 hello -> 1234abd678
//    hello + 3e4a8 -> 1234abd678abc
    public static String md5(String key){

        if(StringUtils.isBlank(key)){
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


}

