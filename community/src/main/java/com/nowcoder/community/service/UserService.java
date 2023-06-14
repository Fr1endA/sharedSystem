package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//service调用dao层接口
//面向接口编程
//调用方法

@Service
public class UserService {


    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);

    }

}
