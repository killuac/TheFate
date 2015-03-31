/**
 * 
 */
package com.woodeck.fate.dao;

import com.woodeck.fate.mapper.UserMapper;
import com.woodeck.fate.model.User;

/**
 * @author Killua
 *
 */
public class UserDAOImpl implements UserDAO {
	
	private UserMapper userMapper;
	
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
	
	@Override
	public User getUserByName(String userName) {
		return userMapper.selectByUserName(userName);
	}
	
	@Override
	public int getUserCount() {
		return userMapper.selectUserCount();
	}
	
	@Override
	public int createUser(String userName, String password) {
		User oneUser = new User();
		oneUser.setUserName(userName);
		oneUser.setPassword(password);
		oneUser.setNickName(userName);
		if (this.getUserCount() < 500) oneUser.setGoldCoin(200);
		
		return userMapper.insert(oneUser);
	}
	
	@Override
	public int updateUser(User record) {
		if (!record.isVip()) {
			record.setIsVip(false);
			record.setDiscount(0.0f);
			record.setAddEpRate(0.0f);
		}
		return userMapper.updateByPrimaryKeySelective(record);
	}
	
	
//	public static void main(String[] args) {
//		int count = BeanHelper.getUserBean().getUserCount();
//		System.out.println(count);
//	}
	
}
