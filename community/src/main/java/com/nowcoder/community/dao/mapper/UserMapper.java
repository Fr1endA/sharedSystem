package com.nowcoder.community.dao.mapper;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;


//Spring容器自动扫描Mapper注解，由Mapper解释器 （.xml）解释执行相应的sql语句
@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String userName);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id , String headerUrl);

    int updatePassword(int id,String password);


}


