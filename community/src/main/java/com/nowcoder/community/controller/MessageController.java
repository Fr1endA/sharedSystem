package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.until.CommunityUtil;
import com.nowcoder.community.until.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

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
        if(message.getFromId()< message.getToId()){
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

}
