/**
 * 
 */
package com.woodeck.fate.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.woodeck.fate.dao.BuyHistoryDAO;
import com.woodeck.fate.dao.FeedbackDAO;
import com.woodeck.fate.dao.LevelExpDAO;
import com.woodeck.fate.dao.LoginHistoryDAO;
import com.woodeck.fate.dao.UserDAO;
import com.woodeck.fate.dao.UserHeroDAO;

/**
 * @author Killua
 *
 */
public class BeanHelper {
	
//	private static ApplicationContext appContext = new FileSystemXmlApplicationContext("config/applicationContext.xml");
	private static ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
	
	public static UserDAO getUserBean() {
	    return appContext.getBean(UserDAO.class);
	}
	
	public static UserHeroDAO getUserHeroBean() {
		return appContext.getBean(UserHeroDAO.class);
	}
	
	public static LevelExpDAO getLevelExpBean() {
		return appContext.getBean(LevelExpDAO.class);
	}
	
	public static FeedbackDAO getFeedbackBean() {
		return appContext.getBean(FeedbackDAO.class);
	}
	
	public static LoginHistoryDAO getLoginHistoryBean() {
		return appContext.getBean(LoginHistoryDAO.class);
	}
	
	public static BuyHistoryDAO getBuyHisotoryBean() {
		return appContext.getBean(BuyHistoryDAO.class);
	}
	
}
