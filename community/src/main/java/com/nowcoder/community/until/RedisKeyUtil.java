package com.nowcoder.community.until;

public class RedisKeyUtil {

    private static final String SPLIT = ":";

    private static final String PREFIX_ENTRY_LIKE= "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWER= "follower";
    private static final String PREFIX_FOLLOWEE="followee";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";
    // 某个实体的赞，变量
    //like:entity:entityType:entityId  > set{userId} (存储点赞用户的Id) 获取集合数量，即为赞的数量
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTRY_LIKE + SPLIT + entityType+ SPLIT + entityId;
    }

    //某个用户的赞
    //like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId ;
    }

    //某个用户关注的某个实体,用户关注目标
    //followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE+ SPLIT+ userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝们，某实体的粉丝们
    //follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType + SPLIT + entityId;
    }

    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT +userId;
    }

}
