package com.nowcoder.community.controller;



import com.nowcoder.community.service.AlphaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello SpringBoot";
    }


    @RequestMapping("/data")
    @ResponseBody
    public String test(){
        return alphaService.find();
    }

//    用request和response对象获取和处理http请求数据
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
//        获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration=request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name=enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));

//          返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
//                自动close writer资源
                PrintWriter writer=response.getWriter();
                ){
            writer.write("<h1>牛客网<h1>");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

//    GET方法获取数据
//    http://localhost:8080/community/alpha/students?current=2&limit=3
    @RequestMapping(path="/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam (name="current",required = false,defaultValue = "1") int current,
            @RequestParam(name="limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);

        return "anything is ok ";
    }

    @RequestMapping(path="/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

//    POST请求
    @RequestMapping(path="/transaction",method=RequestMethod.POST)
    @ResponseBody
    public String getTransaction(String userId,String psw){

        System.out.println(userId);
        System.out.println(psw);

        return "success";

    }

    //响应html 动态html
    @RequestMapping(path = "/teacher",method=RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav =new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(path ="/school",method=RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北大");
        model.addAttribute("age",80);
        return "/demo/view";
    }


//    响应Json 数据
//    Java对象→ Json字符串→js对象
//    ResponseBody→json，若无，则可能是html
    @RequestMapping(path="/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> map=new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",8000.00);
        return map;
    }
    @RequestMapping(path="/emps",method=RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){

        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",8000.00);
        list.add(map);

        map=new HashMap<>();
        map.put("name","李四");
        map.put("age",24);
        map.put("salary",9000.00);
        list.add(map);

        map=new HashMap<>();
        map.put("name","王五");
        map.put("age",25);
        map.put("salary",10000.00);
        list.add(map);

        return list;
    }
}
