package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);
		AlphaDao alphaDao=applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao);
		System.out.println(alphaDao.select());
		alphaDao=applicationContext.getBean("hibernates",AlphaDao.class);
		System.out.println(alphaDao);
		System.out.println(alphaDao.select());


	}

	@Test
	public void testBeanManagement(){
		AlphaService alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
		alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testLeeCode(){


		class Solution {
			public static int[][] generateMatrix(int n) {

				int[][] array=new int[n][n];
				int circle=n/2;
				circle+=n%2==0?0:1;
				int round=0;
				int num=1;

				// 全部循环
				while(round<circle){

					int i,j;
					for(j= round;j<n-1-round;j++){
						array[round][j]=num;
						num++;
					}

					for(i=round;i<n-1-round;i++){
						array[i][j]=num;
						num++;
					}

					for(j=n-1-round;j>round;j--){
						array[i][j]=num;
						num++;
					}
					for(i=n-1-round;i>round;i--){
						array[i][j]=num;
						num++;
					}
					if(num>n*n){
						break;
					}
					round++;
				}
				return array;
			}
		}
		int[][] array=new int[3][3];

		array=Solution.generateMatrix(3);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(array[i][j] + ",");
			}
			System.out.println("\n");
		}
	}

}
