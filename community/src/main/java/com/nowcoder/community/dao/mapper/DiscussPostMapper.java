package com.nowcoder.community.dao.mapper;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

//    @param 取别名
//    如果只有一个参数，并且在<if>里使用，必须使用别名
    int selectDiscussPostRows(@Param("userId") int userId);


}
