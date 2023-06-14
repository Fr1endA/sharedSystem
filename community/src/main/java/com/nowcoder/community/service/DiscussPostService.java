package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.dao.mapper.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper mapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset,int limit){
        return mapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostsRows(int userId){
        return mapper.selectDiscussPostRows(userId);
    }

}
