package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.dao.mapper.DiscussPostMapper;
import com.nowcoder.community.until.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper mapper;
    @Autowired
    private SensitiveFilter filter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset,int limit){
        return mapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostsRows(int userId){
        return mapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost){
        if(discussPost==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(filter.filter(discussPost.getTitle()));
        discussPost.setContent(filter.filter(discussPost.getContent()));

//        //默认值设置于Controller层
//        discussPost.setUserId(hostHolder.getUser().getId());
//        discussPost.setCreateTime(new Date());
//
        return mapper.insertDiscussPost(discussPost);

    }

    public DiscussPost getDiscussPostById(int id){
        return mapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int discussPostId,int count){

        return mapper.updateCommentCount(discussPostId,count);

    }

}
