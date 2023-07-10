package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.until.CommunityUtil;
import com.nowcoder.community.until.HostHolder;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping(path="/user")
public class UserController {


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path="/setting",method= RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path="/upload",method=RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","尚未选择图片");
            return "/site/setting";
        }
        String filename=headerImage.getOriginalFilename();
        String suffix=filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){

            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        filename=CommunityUtil.generateUUID()+suffix;
        // 确定文件存放路径
        File dest=new File(uploadPath+"/"+filename);

        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常",e);
        }

        //存储成功,更新当前头像访问路径（web访问路径）
        //http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        //user存储的是服务器的访问路径
        //服务器中存取的是特定路径
        String headerUrl= domain+ contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);


        return "redirect:/index";
    }


    @RequestMapping(path="/header/{filename}",method=RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){

        //服务器存放路径 就是 uploadPath + "/" + filename
        filename=uploadPath + "/" + filename;
        //解析后缀

        String suffix=filename.substring(filename.lastIndexOf("."));
        //响应图片

        response.setContentType("image/"+suffix);
        try (
                FileInputStream fis=new FileInputStream(filename);
                ){
            OutputStream os=response.getOutputStream();

            byte[] buffer=new byte[1024];
            int b =0 ;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取图像失败"+e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path="/updatepsw",method = RequestMethod.POST)
    public String updatePassword(String password,String newPassword,Model model){
        User user=hostHolder.getUser();
        password=CommunityUtil.md5(password + user.getSalt());
        if(password==null){
            model.addAttribute("passwordMsg","密码不能为空");
            return "/site/setting";
        }
        if(!user.getPassword().equals(password)){
            model.addAttribute("passwordMsg","密码不正确");
            return "/site/setting";
        }
        if(newPassword==null){
            model.addAttribute("newPasswordMsg","新密码不能为空");
            return "/site/setting";
        }
        newPassword=CommunityUtil.md5(newPassword+user.getSalt());
        userService.updatePassword(user.getId(),newPassword);
        return "redirect:/logout";
    }



}
