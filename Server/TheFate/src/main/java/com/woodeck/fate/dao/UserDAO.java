package com.woodeck.fate.dao;

import com.woodeck.fate.model.User;

public interface UserDAO {
	
	User getUserByName(String userName);
	
	int createUser(String userName, String password);
	
	int updateUser(User record);
	
	int getUserCount();
	
}