package com.nowcoder.community.controller;

import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.until.CommunityConstant;
import com.nowcoder.community.until.CommunityUtil;
import com.nowcoder.community.until.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired

    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;
//    处理私信列表请求
    @Autowired
    private UserService userService;
    @RequestMapping(path="/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){

        User user=hostHolder.getUser();
//     分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
//      会话列表
        List<Message> conversationList=
                messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());

        List<Map<String,Object>> conversations=new ArrayList<>();

        if(conversationList!=null){
            for(Message message:conversationList){
                Map<String,Object> map= new HashMap<>();
                map.put("conversation",message);
//                查询某个会话的未读消息
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
//                会话消息总数
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                int targetId = user.getId() == message.getFromId()? message.getToId() : message.getFromId();

                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

//        查询整个用户所有的未读消息数量
        int unreadCount= messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",unreadCount);
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";

    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(Model model, @PathVariable("conversationId" ) String conversationId,Page page){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        List<Message> letterList=messageService.findLetters(conversationId,page.getOffset(),page.getLimit());

        List<Map<String,Object>> letters = new ArrayList<>();

        if(letterList!=null){
            for(Message message : letterList){
                Map<String,Object> map= new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));

                letters.add(map);
            }
        }
//        设置私信
        model.addAttribute("letters",letters);
//        设置目标
        model.addAttribute("target",getLetterTarget(conversationId));
//        获得私信id，并对每个读到的私信id标志已读
        List<Integer> ids=getLettersIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }


        return "/site/letter-detail";

    }
    private List<Integer> getLettersIds(List<Message> letters){
        List<Integer> ids=new ArrayList<>();

        if (letters != null) {
            for (Message message : letters) {
                if (message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;

    }
    private User getLetterTarget(String conversationId){
        String[] ids=conversationId.split("_");
        int id0=Integer.valueOf(ids[0]);
        int id1=Integer.valueOf(ids[1]);
        int finalId=hostHolder.getUser().getId()==id0?id1:id0;

        User user=userService.findUserById(finalId);
        return user;
    }

    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target=userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }

        Message message=new Message();
        message.setStatus(0);
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_" + message.getToId());
        } else{
            message.setConversationId(message.getToId()+"_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);

    }

    @RequestMapping(path="/letter/delete" ,method = RequestMethod.POST)
    @ResponseBody
    public String deleteLetter(int id){

        if(id>0){
            messageService.deleteMessage(id);
        }else{
            return CommunityUtil.getJSONString(1,"删除失败，请重试");
        }
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path="/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user= hostHolder.getUser();

        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if(message!=null){
            //从数据表中找到最新的数据行，可以获取message的各个字段。
            // 其中content行为JSON字符串，需要还原为对象
            messageVO.put("message", message);
            //将JSON字符串去转义字符。
            String content = HtmlUtils.htmlUnescape(message.getContent());
            //从去除转义字符的JSON字符串中，还原对象（用Map接收）
            Map<String, Object> data=JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType",(Integer)data.get("entityType"));
            messageVO.put("entityId",(Integer) data.get ("entityId"));
            messageVO.put("postId",(Integer) data.get ("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread",unread);

        }else{
            messageVO.put("message", null);
        }
        model.addAttribute("commentNotice", messageVO);
        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        if(message!=null){
            //从数据表中找到最新的数据行，可以获取message的各个字段。
            // 其中content行为JSON字符串，需要还原为对象
            messageVO.put("message", message);
            //将JSON字符串去转义字符。
            String content = HtmlUtils.htmlUnescape(message.getContent());
            //从去除转义字符的JSON字符串中，还原对象（用Map接收）
            Map<String, Object> data=JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType",(Integer)data.get("entityType"));
            messageVO.put("entityId",(Integer) data.get ("entityId"));
            messageVO.put("postId",(Integer) data.get ("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread",unread);

        }else{
            messageVO.put("message", null);
        }
        model.addAttribute("likeNotice", messageVO);
        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if(message!=null){
            //从数据表中找到最新的数据行，可以获取message的各个字段。
            // 其中content行为JSON字符串，需要还原为对象
            messageVO.put("message", message);
            //将JSON字符串去转义字符。
            String content = HtmlUtils.htmlUnescape(message.getContent());
            //从去除转义字符的JSON字符串中，还原对象（用Map接收）
            Map<String, Object> data=JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType",(Integer)data.get("entityType"));
            messageVO.put("entityId",(Integer) data.get ("entityId"));


            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread",unread);

        }else{
            messageVO.put("message", null);
        }
        model.addAttribute("followNotice", messageVO);

        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);


        return "/site/notice";
    }

    @RequestMapping(path="/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){

        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());

        List<Map<String, Object>> noticesVOList = new ArrayList<>();
        if(noticeList!=null){
            for(Message notice : noticeList){
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);

                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String , Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer)data.get("userId")) );
                map.put("entityType", (Integer)data.get("entityType"));
                map.put("entityId",(Integer)data.get("entityId"));
                map.put("postId", (Integer)data.get("postId"));

                //通知作者

                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticesVOList.add(map);
            }


        }
        model.addAttribute("notices", noticesVOList);

        // 设置已读
        List<Integer> ids= getLettersIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";

    }

}
