package com.nowcoder.community;

import jakarta.annotation.PostConstruct;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan({"com.nowcoder.community.mapper"})
//@MapperScan("com.nowcoder.community.mapper")

public class CommunityApplication {

	@PostConstruct
//	解决netty启动冲突的问题（redis和es的netty的共同依赖）
	public void init(){
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
