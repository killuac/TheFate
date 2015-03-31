/**
 * 
 */
package com.woodeck.fate.dao;

import java.util.Date;

import com.woodeck.fate.mapper.LoginHistoryMapper;
import com.woodeck.fate.model.LoginHistory;
import com.woodeck.fate.model.User;
import com.woodeck.fate.util.BeanHelper;

/**
 * @author Killua
 *
 */
public class LoginHistoryDAOImpl implements LoginHistoryDAO {

	private LoginHistoryMapper loginHistoryMapper;
	
	public void setLoginHistoryMapper(LoginHistoryMapper loginHistoryMapper) {
		this.loginHistoryMapper = loginHistoryMapper;
	}	
	
	@Override
	public void addHistory(Integer userId, String ipAddress) {
		LoginHistory history = new LoginHistory();
		history.setUserId(userId);
		history.setIpAddress(ipAddress);
		loginHistoryMapper.insertSelective(history);
	}

	@Override
	public void updateHistory(String userName) {
		User user = BeanHelper.getUserBean().getUserByName(userName);
		LoginHistory history = loginHistoryMapper.selectByUserId(user.getUserId());
		history.setLogoutTime(new Date());
		loginHistoryMapper.updateByPrimaryKeySelective(history);
	}
	
	public static void main(String[] args) {
		BeanHelper.getLoginHistoryBean().updateHistory("killua");
	}

}
