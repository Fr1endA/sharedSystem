package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.until.CommunityConstant;
import com.nowcoder.community.until.CommunityUtil;
import com.nowcoder.community.until.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path="/add",method= RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        if(content==null||title==null){
            return CommunityUtil.getJSONString(403,"获取数据失败");
        }
        User user= hostHolder.getUser();

        DiscussPost post = new DiscussPost();
        post.setCreateTime(new Date());
        post.setContent(content);
        post.setTitle(title);
        post.setUserId(user.getId());
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0,"发布成功！");

    }

    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDisscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page ){
//      Page自动注入Model中
//      在每条post中，显示该post的实体信息和user实体信息
//      postView 和 userView
        DiscussPost post= discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        User user=userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

//      分页显示评论信息
        page.setPath("/discuss/detail/"+ discussPostId);
        page.setLimit(5);
        page.setRows(post.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment> commentsList=commentService.findCommentByEntity(
                ENTITY_TYPE_POST,discussPostId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> commentsVOList= new ArrayList<>() ;
        if(commentsList!=null){
            for(Comment comment: commentsList){
                Map<String, Object> cvo=new HashMap<>();
//                评论
                cvo.put("comment",comment);
//                发布评论的用户
                cvo.put("user",userService.findUserById(comment.getUserId()));
//                该评论的回复列表
                List<Comment> replysList=commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVOList= new ArrayList<>();
                if(replysList!=null){
                    for(Comment reply: replysList){
                        Map<String,Object> replyVO=new HashMap<>();
//                        某条回复具体实体信息
                        replyVO.put("reply",reply);
//                        发布该回复的用户
                        replyVO.put("user",userService.findUserById(reply.getUserId()));
//                        回复的目标用户,如果目标不存在，则为null，否则为具体的用户
                        User target=reply.getTargetId()==0?null:userService.findUserById(reply.getTargetId());
//                        将目标用户存入ViewObject中
                        replyVO.put("target",target);
//                        将该部分ViewObject添加到ViewObject列表中
                        replyVOList.add(replyVO);
                    }
                }
//                将该回复列表加入评论的ViewObject中
                cvo.put("replys",replyVOList);
                int replysCount=commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                cvo.put("replysCount",replysCount);
                commentsVOList.add(cvo);

            }
        }

        model.addAttribute("comments",commentsVOList);


        return "/site/discuss-detail";
    }


}
